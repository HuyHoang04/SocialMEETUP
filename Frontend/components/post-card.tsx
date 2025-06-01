"use client"

import { useState, useEffect } from "react"
import Image from "next/image"
import Link from "next/link"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import {
  Heart,
  MessageCircle,
  Share,
  MoreHorizontal,
  Bookmark,
  ThumbsUp,
  Laugh,
  Angry,
  FrownIcon as Sad,
} from "lucide-react"
import { apiClient, byteArrayToImageUrl } from "@/lib/api"
import type { PostResponse, PostReactionResponse, CommentResponse } from "@/lib/api"

interface PostCardProps {
  post:
    | PostResponse
    | {
        id: string
        author: {
          id: string
          username: string
          fullName: string
          avatar: string
        }
        content: string
        images: string[]
        createdAt: string
        reactions: Array<{ type: string; count: number }>
        commentCount: number
        privacySetting: string
      }
}

const reactionIcons = {
  LIKE: ThumbsUp,
  LOVE: Heart,
  HAHA: Laugh,
  WOW: "😮",
  SAD: Sad,
  ANGRY: Angry,
}

export function PostCard({ post }: PostCardProps) {
  const [isLiked, setIsLiked] = useState(false)
  const [isSaved, setIsSaved] = useState(false)
  const [showComments, setShowComments] = useState(false)
  const [reactions, setReactions] = useState<PostReactionResponse[]>([])
  const [comments, setComments] = useState<CommentResponse[]>([])
  const [newComment, setNewComment] = useState("")

  // Check if this is a PostResponse or legacy format
  const isPostResponse = "createAt" in post && "author" in post && post.author && "fullname" in post.author

  // Get author info with safe fallbacks
  const authorInfo = isPostResponse
    ? {
        id: post.author?.id || "unknown",
        username: post.author?.username || "unknown",
        fullname: post.author?.fullname || "Unknown User",
        avatar:
          post.author?.avatar && post.author?.avatarType
            ? byteArrayToImageUrl(post.author.avatar, post.author.avatarType)
            : "/placeholder.svg?height=40&width=40",
      }
    : {
        id: (post as any).author?.id || "unknown",
        username: (post as any).author?.username || "unknown",
        fullname: (post as any).author?.fullName || "Unknown User",
        avatar: (post as any).author?.avatar || "/placeholder.svg?height=40&width=40",
      }

  // Get post content with safe fallbacks
  const postContent = post.content || ""
  const postDate = isPostResponse ? post.createAt : (post as any).createdAt || new Date().toISOString()

  // Get post image URL from byte array (for PostResponse) or images array (for legacy)
  const postImageUrl = isPostResponse
    ? post.imageData && post.imageType
      ? byteArrayToImageUrl(post.imageData, post.imageType)
      : null
    : (post as any).images && (post as any).images.length > 0
      ? (post as any).images[0]
      : null

  useEffect(() => {
    // Only load reactions and comments for real PostResponse objects
    if (isPostResponse) {
      loadReactions()
      if (showComments) {
        loadComments()
      }
    }
  }, [post.id, showComments, isPostResponse])

  const loadReactions = async () => {
    try {
      const response = await apiClient.getPostReactions(post.id)
      if (response.code === "200") {
        setReactions(response.result)
      }
    } catch (error) {
      console.error("Error loading reactions:", error)
    }
  }

  const loadComments = async () => {
    try {
      const response = await apiClient.getCommentsByPost(post.id)
      if (response.code === "200") {
        setComments(response.result)
      }
    } catch (error) {
      console.error("Error loading comments:", error)
    }
  }

  const handleReaction = async (type: "LIKE" | "LOVE" | "HAHA" | "WOW" | "SAD" | "ANGRY") => {
    try {
      const currentUserId = "user1" // Get from auth context

      if (isLiked) {
        // Remove reaction
        await apiClient.deletePostReaction(currentUserId, post.id)
        setIsLiked(false)
      } else {
        // Add/update reaction
        await apiClient.createOrUpdatePostReaction({
          userId: currentUserId,
          postId: post.id,
          reactionType: type,
        })
        setIsLiked(true)
      }

      // Reload reactions
      loadReactions()
    } catch (error) {
      console.error("Error updating reaction:", error)
    }
  }

  const handleAddComment = async () => {
    if (!newComment.trim()) return

    try {
      const currentUserId = "user1" // Get from auth context

      const response = await apiClient.createComment({
        authorId: currentUserId,
        postId: post.id,
        content: newComment,
      })

      if (response.code === "201") {
        setNewComment("")
        loadComments() // Reload comments
      }
    } catch (error) {
      console.error("Error adding comment:", error)
    }
  }

  const handleSave = () => {
    setIsSaved(!isSaved)
    // In real app: call API to save/unsave post
  }

  const handleShare = () => {
    // In real app: implement sharing functionality
    navigator.share?.({
      title: `Post by ${authorInfo.fullname}`,
      text: postContent,
      url: window.location.href,
    })
  }

  // Group reactions by type and count (for PostResponse)
  const reactionCounts = reactions.reduce(
    (acc, reaction) => {
      acc[reaction.reactionType] = (acc[reaction.reactionType] || 0) + 1
      return acc
    },
    {} as Record<string, number>,
  )

  // For legacy format, use the reactions array
  const legacyReactions = !isPostResponse ? (post as any).reactions || [] : []
  const totalReactions = isPostResponse
    ? reactions.length
    : legacyReactions.reduce((sum: number, r: any) => sum + (r.count || 0), 0)
  const commentCount = isPostResponse ? comments.length : (post as any).commentCount || 0

  return (
    <Card className="w-full">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Avatar>
              <AvatarImage src={authorInfo.avatar || "/placeholder.svg"} alt={authorInfo.fullname} />
              <AvatarFallback>
                {authorInfo.fullname
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </AvatarFallback>
            </Avatar>
            <div>
              <Link href={`/profile/${authorInfo.id}`} className="font-semibold hover:underline">
                {authorInfo.fullname}
              </Link>
              <p className="text-sm text-gray-600">
                @{authorInfo.username} • {new Date(postDate).toLocaleDateString()}
              </p>
            </div>
          </div>

          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="sm">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={handleSave}>
                <Bookmark className="mr-2 h-4 w-4" />
                {isSaved ? "Unsave" : "Save"} Post
              </DropdownMenuItem>
              <DropdownMenuItem>Hide Post</DropdownMenuItem>
              <DropdownMenuItem>Report Post</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {/* Post Content */}
        <div>
          <p className="text-gray-900 whitespace-pre-wrap">{postContent}</p>
        </div>

        {/* Post Image */}
        {postImageUrl && (
          <div className="relative aspect-video rounded-lg overflow-hidden">
            <Image src={postImageUrl || "/placeholder.svg"} alt="Post image" fill className="object-cover" />
          </div>
        )}

        {/* Reaction Summary */}
        {totalReactions > 0 && (
          <div className="flex items-center justify-between text-sm text-gray-600 border-b pb-2">
            <div className="flex items-center gap-1">
              <div className="flex -space-x-1">
                {isPostResponse
                  ? Object.entries(reactionCounts)
                      .slice(0, 3)
                      .map(([type, count]) => {
                        const Icon = reactionIcons[type as keyof typeof reactionIcons]
                        return (
                          <div key={type} className="bg-blue-500 text-white rounded-full p-1 text-xs">
                            {typeof Icon === "string" ? Icon : <Icon className="h-3 w-3" />}
                          </div>
                        )
                      })
                  : legacyReactions.slice(0, 3).map((reaction: any, index: number) => {
                      const Icon = reactionIcons[reaction.type as keyof typeof reactionIcons]
                      return (
                        <div key={index} className="bg-blue-500 text-white rounded-full p-1 text-xs">
                          {typeof Icon === "string" ? Icon : <Icon className="h-3 w-3" />}
                        </div>
                      )
                    })}
              </div>
              <span>{totalReactions} reactions</span>
            </div>
            <div className="flex gap-4">
              <span>{commentCount} comments</span>
              <span>12 shares</span>
            </div>
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex items-center justify-between">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => handleReaction("LIKE")}
            className={`flex-1 ${isLiked ? "text-blue-600" : "text-gray-600"}`}
          >
            <ThumbsUp className={`mr-2 h-4 w-4 ${isLiked ? "fill-current" : ""}`} />
            Like
          </Button>

          <Button
            variant="ghost"
            size="sm"
            onClick={() => setShowComments(!showComments)}
            className="flex-1 text-gray-600"
          >
            <MessageCircle className="mr-2 h-4 w-4" />
            Comment
          </Button>

          <Button variant="ghost" size="sm" onClick={handleShare} className="flex-1 text-gray-600">
            <Share className="mr-2 h-4 w-4" />
            Share
          </Button>
        </div>

        {/* Comments Section */}
        {showComments && (
          <div className="border-t pt-4 space-y-3">
            <div className="flex gap-3">
              <Avatar className="h-8 w-8">
                <AvatarImage src="/placeholder.svg?height=32&width=32" alt="You" />
                <AvatarFallback>You</AvatarFallback>
              </Avatar>
              <div className="flex-1 flex gap-2">
                <input
                  type="text"
                  placeholder="Write a comment..."
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  className="flex-1 bg-gray-100 rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onKeyPress={(e) => e.key === "Enter" && handleAddComment()}
                />
                <Button size="sm" onClick={handleAddComment} disabled={!newComment.trim()}>
                  Post
                </Button>
              </div>
            </div>

            {/* Comments List */}
            <div className="space-y-3">
              {comments.map((comment) => {
                const commentAvatarUrl =
                  comment.author?.avatar && comment.author?.avatarType
                    ? byteArrayToImageUrl(comment.author.avatar, comment.author.avatarType)
                    : "/placeholder.svg?height=32&width=32"

                return (
                  <div key={comment.id} className="flex gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage
                        src={commentAvatarUrl || "/placeholder.svg"}
                        alt={comment.author?.fullname || "User"}
                      />
                      <AvatarFallback>
                        {(comment.author?.fullname || "U")
                          .split(" ")
                          .map((n) => n[0])
                          .join("")}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1">
                      <div className="bg-gray-100 rounded-lg px-3 py-2">
                        <p className="font-semibold text-sm">{comment.author?.fullname || "Unknown User"}</p>
                        <p className="text-sm">{comment.content}</p>
                        {comment.imageData && comment.imageType && (
                          <div className="mt-2">
                            <Image
                              src={byteArrayToImageUrl(comment.imageData, comment.imageType) || "/placeholder.svg"}
                              alt="Comment image"
                              width={200}
                              height={150}
                              className="rounded-lg"
                            />
                          </div>
                        )}
                      </div>
                      <div className="flex gap-4 mt-1 text-xs text-gray-600">
                        <button className="hover:underline">Like</button>
                        <button className="hover:underline">Reply</button>
                        <span>{new Date(comment.createAt).toLocaleTimeString()}</span>
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
