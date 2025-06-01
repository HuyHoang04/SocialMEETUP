"use client"

import type React from "react"

import { useState } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ImageIcon, MapPin, Smile, Globe, Users, Lock } from "lucide-react"
import { apiClient } from "@/lib/api"

interface CreatePostProps {
  onPostCreated: (post: any) => void
}

export function CreatePost({ onPostCreated }: CreatePostProps) {
  const [content, setContent] = useState("")
  const [privacy, setPrivacy] = useState("PUBLIC")
  const [isPosting, setIsPosting] = useState(false)

  const currentUser = {
    id: "user1",
    username: "john_doe",
    fullName: "John Doe",
    avatar: "/placeholder.svg?height=40&width=40",
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!content.trim()) return

    setIsPosting(true)

    try {
      // Create post using API
      const postData = {
        authorId: currentUser.id,
        content,
        privacySetting: privacy,
        // Add other fields as needed based on PostCreateRequest
      }

      const response = await apiClient.createPost(postData)

      if (response.code === "201") {
        onPostCreated(response.result)
        setContent("")
        setPrivacy("PUBLIC")
      }
    } catch (error) {
      console.error("Error creating post:", error)
    } finally {
      setIsPosting(false)
    }
  }

  const privacyOptions = [
    { value: "PUBLIC", label: "Public", icon: Globe },
    { value: "FRIENDS", label: "Friends", icon: Users },
    { value: "PRIVATE", label: "Only me", icon: Lock },
  ]

  return (
    <Card>
      <CardContent className="p-4">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="flex gap-3">
            <Avatar>
              <AvatarImage src={currentUser.avatar || "/placeholder.svg"} alt={currentUser.fullName} />
              <AvatarFallback>
                {currentUser.fullName
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <Textarea
                placeholder="What's on your mind?"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                className="min-h-[100px] resize-none border-none shadow-none focus-visible:ring-0 text-lg placeholder:text-gray-500"
              />
            </div>
          </div>

          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Button type="button" variant="ghost" size="sm">
                <ImageIcon className="h-5 w-5 text-green-600" />
                <span className="ml-2 text-green-600">Photo</span>
              </Button>
              <Button type="button" variant="ghost" size="sm">
                <MapPin className="h-5 w-5 text-red-600" />
                <span className="ml-2 text-red-600">Location</span>
              </Button>
              <Button type="button" variant="ghost" size="sm">
                <Smile className="h-5 w-5 text-yellow-600" />
                <span className="ml-2 text-yellow-600">Feeling</span>
              </Button>
            </div>

            <div className="flex items-center gap-3">
              <Select value={privacy} onValueChange={setPrivacy}>
                <SelectTrigger className="w-32">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {privacyOptions.map((option) => (
                    <SelectItem key={option.value} value={option.value}>
                      <div className="flex items-center gap-2">
                        <option.icon className="h-4 w-4" />
                        {option.label}
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              <Button type="submit" disabled={!content.trim() || isPosting} className="px-6">
                {isPosting ? "Posting..." : "Post"}
              </Button>
            </div>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
