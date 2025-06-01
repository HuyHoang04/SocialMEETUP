import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Home, User, Users, MessageCircle, Bookmark, Settings, TrendingUp } from "lucide-react"

const currentUser = {
  id: "user1",
  username: "john_doe",
  fullName: "John Doe",
  avatar: "/placeholder.svg?height=40&width=40",
}

const navigationItems = [
  { icon: Home, label: "Home", href: "/" },
  { icon: User, label: "Profile", href: `/profile/${currentUser.id}` },
  { icon: Users, label: "Friends", href: "/friends" },
  { icon: MessageCircle, label: "Messages", href: "/chat" },
  { icon: Bookmark, label: "Saved", href: "/saved" },
  { icon: Settings, label: "Settings", href: "/settings" },
]

const trendingTopics = ["#ReactJS", "#WebDevelopment", "#JavaScript", "#TechNews", "#Programming"]

export function Sidebar() {
  return (
    <div className="space-y-4">
      {/* User Profile Card */}
      <Card>
        <CardContent className="p-4">
          <Link
            href={`/profile/${currentUser.id}`}
            className="flex items-center gap-3 hover:bg-gray-50 p-2 rounded-lg transition-colors"
          >
            <Avatar>
              <AvatarImage src={currentUser.avatar || "/placeholder.svg"} alt={currentUser.fullName} />
              <AvatarFallback>
                {currentUser.fullName
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </AvatarFallback>
            </Avatar>
            <div>
              <h3 className="font-semibold">{currentUser.fullName}</h3>
              <p className="text-sm text-gray-600">@{currentUser.username}</p>
            </div>
          </Link>
        </CardContent>
      </Card>

      {/* Navigation */}
      <Card>
        <CardContent className="p-2">
          <nav className="space-y-1">
            {navigationItems.map((item) => (
              <Button key={item.href} variant="ghost" className="w-full justify-start" asChild>
                <Link href={item.href}>
                  <item.icon className="mr-3 h-5 w-5" />
                  {item.label}
                </Link>
              </Button>
            ))}
          </nav>
        </CardContent>
      </Card>

      {/* Trending Topics */}
      <Card>
        <CardContent className="p-4">
          <div className="flex items-center gap-2 mb-3">
            <TrendingUp className="h-5 w-5 text-blue-600" />
            <h3 className="font-semibold">Trending</h3>
          </div>
          <div className="space-y-2">
            {trendingTopics.map((topic) => (
              <Button
                key={topic}
                variant="ghost"
                size="sm"
                className="w-full justify-start text-blue-600 hover:text-blue-700 hover:bg-blue-50"
              >
                {topic}
              </Button>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
