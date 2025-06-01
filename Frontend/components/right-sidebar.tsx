import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { UserPlus, Calendar, MapPin } from "lucide-react"

const suggestedFriends = [
  {
    id: "user4",
    username: "alice_johnson",
    fullName: "Alice Johnson",
    avatar: "/placeholder.svg?height=40&width=40",
    mutualFriends: 5,
  },
  {
    id: "user5",
    username: "bob_smith",
    fullName: "Bob Smith",
    avatar: "/placeholder.svg?height=40&width=40",
    mutualFriends: 3,
  },
  {
    id: "user6",
    username: "carol_white",
    fullName: "Carol White",
    avatar: "/placeholder.svg?height=40&width=40",
    mutualFriends: 8,
  },
]

const upcomingEvents = [
  {
    id: "1",
    title: "Tech Meetup",
    date: "2024-01-20",
    time: "18:00",
    location: "Downtown Conference Center",
  },
  {
    id: "2",
    title: "React Workshop",
    date: "2024-01-25",
    time: "14:00",
    location: "Online",
  },
]

export function RightSidebar() {
  return (
    <div className="space-y-4">
      {/* Friend Suggestions */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">People You May Know</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {suggestedFriends.map((friend) => (
            <div key={friend.id} className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Avatar className="h-10 w-10">
                  <AvatarImage src={friend.avatar || "/placeholder.svg"} alt={friend.fullName} />
                  <AvatarFallback>
                    {friend.fullName
                      .split(" ")
                      .map((n) => n[0])
                      .join("")}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <h4 className="font-medium text-sm">{friend.fullName}</h4>
                  <p className="text-xs text-gray-600">{friend.mutualFriends} mutual friends</p>
                </div>
              </div>
              <Button size="sm" variant="outline">
                <UserPlus className="h-4 w-4" />
              </Button>
            </div>
          ))}
          <Button variant="ghost" className="w-full text-blue-600">
            See All Suggestions
          </Button>
        </CardContent>
      </Card>

      {/* Upcoming Events */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Upcoming Events</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {upcomingEvents.map((event) => (
            <div key={event.id} className="border-l-4 border-blue-500 pl-3">
              <h4 className="font-medium text-sm">{event.title}</h4>
              <div className="flex items-center gap-1 text-xs text-gray-600 mt-1">
                <Calendar className="h-3 w-3" />
                {new Date(event.date).toLocaleDateString()} at {event.time}
              </div>
              <div className="flex items-center gap-1 text-xs text-gray-600">
                <MapPin className="h-3 w-3" />
                {event.location}
              </div>
            </div>
          ))}
          <Button variant="ghost" className="w-full text-blue-600">
            View All Events
          </Button>
        </CardContent>
      </Card>

      {/* Quick Stats */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Your Activity</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">Posts this week</span>
            <Badge variant="secondary">12</Badge>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">Profile views</span>
            <Badge variant="secondary">89</Badge>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">New followers</span>
            <Badge variant="secondary">+5</Badge>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
