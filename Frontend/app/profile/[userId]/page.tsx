"use client"

import { useState, useEffect } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { PostCard } from "@/components/post-card"
import { MapPin, Calendar, LinkIcon, Users, MessageCircle, Settings } from "lucide-react"
import { ProfileEditModal } from "@/components/profile-edit-modal"

interface ProfilePageProps {
  params: {
    userId: string
  }
}

export default function ProfilePage({ params }: ProfilePageProps) {
  const [user, setUser] = useState({
    id: params.userId,
    username: "john_doe",
    fullName: "John Doe",
    email: "john@example.com",
    bio: "Software Developer | Tech Enthusiast | Coffee Lover ☕",
    location: "San Francisco, CA",
    website: "https://johndoe.dev",
    joinDate: "2023-01-15",
    avatar: "/placeholder.svg?height=120&width=120",
    coverImage: "/placeholder.svg?height=200&width=800",
    followersCount: 1234,
    followingCount: 567,
    postsCount: 89,
  })

  const [userPosts, setUserPosts] = useState([
    {
      id: "1",
      author: {
        id: params.userId,
        username: "john_doe",
        fullName: "John Doe",
        avatar: "/placeholder.svg?height=40&width=40",
      },
      content: "Working on some exciting new features! 🚀",
      images: [],
      createdAt: "2024-01-15T10:30:00Z",
      reactions: [{ type: "LIKE", count: 15 }],
      commentCount: 3,
      privacySetting: "PUBLIC",
    },
  ])

  const [isFollowing, setIsFollowing] = useState(false)

  // Thêm state để handle user updates
  const [currentUser, setCurrentUser] = useState(user)

  const handleUserUpdated = (updatedUser: any) => {
    setCurrentUser(updatedUser)
  }

  useEffect(() => {
    // In real app: fetch user data and posts
    // fetchUserProfile(params.userId)
    // fetchUserPosts(params.userId)
  }, [params.userId])

  const handleFollow = () => {
    setIsFollowing(!isFollowing)
    // In real app: call follow/unfollow API
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      <div className="max-w-4xl mx-auto px-4 py-6">
        {/* Cover Photo & Profile Info */}
        <Card className="mb-6">
          <div
            className="h-48 bg-gradient-to-r from-blue-400 to-purple-500 rounded-t-lg"
            style={{
              backgroundImage: `url(${user.coverImage})`,
              backgroundSize: "cover",
              backgroundPosition: "center",
            }}
          />

          <CardContent className="relative px-6 pb-6">
            <div className="flex flex-col sm:flex-row items-start sm:items-end gap-4 -mt-16">
              <Avatar className="h-32 w-32 border-4 border-white shadow-lg">
                <AvatarImage src={user.avatar || "/placeholder.svg"} alt={user.fullName} />
                <AvatarFallback className="text-2xl">
                  {user.fullName
                    .split(" ")
                    .map((n) => n[0])
                    .join("")}
                </AvatarFallback>
              </Avatar>

              <div className="flex-1 sm:ml-4 mt-4 sm:mt-0">
                <h1 className="text-2xl font-bold">{user.fullName}</h1>
                <p className="text-gray-600">@{user.username}</p>

                <div className="flex gap-6 mt-2 text-sm text-gray-600">
                  <span>
                    <strong>{user.postsCount}</strong> Posts
                  </span>
                  <span>
                    <strong>{user.followersCount}</strong> Followers
                  </span>
                  <span>
                    <strong>{user.followingCount}</strong> Following
                  </span>
                </div>
              </div>

              {/* Thay thế phần buttons trong profile header */}
              <div className="flex gap-2">
                {params.userId === "user1" ? ( // Current user's profile
                  <ProfileEditModal user={currentUser} onUserUpdated={handleUserUpdated}>
                    <Button variant="outline">
                      <Settings className="h-4 w-4 mr-2" />
                      Edit Profile
                    </Button>
                  </ProfileEditModal>
                ) : (
                  <>
                    <Button variant={isFollowing ? "outline" : "default"} onClick={handleFollow}>
                      <Users className="h-4 w-4 mr-2" />
                      {isFollowing ? "Unfollow" : "Follow"}
                    </Button>
                    <Button variant="outline">
                      <MessageCircle className="h-4 w-4 mr-2" />
                      Message
                    </Button>
                  </>
                )}
              </div>
            </div>

            <div className="mt-4">
              <p className="text-gray-700 mb-2">{user.bio}</p>

              <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                {user.location && (
                  <div className="flex items-center gap-1">
                    <MapPin className="h-4 w-4" />
                    {user.location}
                  </div>
                )}
                {user.website && (
                  <div className="flex items-center gap-1">
                    <LinkIcon className="h-4 w-4" />
                    <a
                      href={user.website}
                      className="text-blue-600 hover:underline"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {user.website}
                    </a>
                  </div>
                )}
                <div className="flex items-center gap-1">
                  <Calendar className="h-4 w-4" />
                  Joined {new Date(user.joinDate).toLocaleDateString("en-US", { month: "long", year: "numeric" })}
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Profile Tabs */}
        <Tabs defaultValue="posts" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="posts">Posts</TabsTrigger>
            <TabsTrigger value="media">Media</TabsTrigger>
            <TabsTrigger value="about">About</TabsTrigger>
          </TabsList>

          <TabsContent value="posts" className="space-y-4">
            {userPosts.map((post) => (
              <PostCard key={post.id} post={post} />
            ))}
          </TabsContent>

          <TabsContent value="media">
            <Card>
              <CardHeader>
                <h3 className="text-lg font-semibold">Media</h3>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-3 gap-2">
                  {[1, 2, 3, 4, 5, 6].map((i) => (
                    <div key={i} className="aspect-square bg-gray-200 rounded-lg"></div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="about">
            <Card>
              <CardHeader>
                <h3 className="text-lg font-semibold">About</h3>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <h4 className="font-medium mb-2">Bio</h4>
                  <p className="text-gray-700">{user.bio}</p>
                </div>
                <div>
                  <h4 className="font-medium mb-2">Contact Information</h4>
                  <div className="space-y-2 text-sm">
                    <p>
                      <strong>Email:</strong> {user.email}
                    </p>
                    <p>
                      <strong>Location:</strong> {user.location}
                    </p>
                    <p>
                      <strong>Website:</strong>{" "}
                      <a href={user.website} className="text-blue-600 hover:underline">
                        {user.website}
                      </a>
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}
