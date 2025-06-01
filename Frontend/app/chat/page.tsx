"use client"

import type React from "react"

import { useState, useEffect, useRef } from "react"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Send, Search, Plus, Phone, Video, MoreVertical } from "lucide-react"
import { wsClient } from "@/lib/websocket"
import { apiClient, byteArrayToImageUrl } from "@/lib/api"
import type { MessageCreateRequest, ChatResponse, MessageResponse } from "@/lib/api"

// Mock data with proper response structure
const mockChats: ChatResponse[] = [
  {
    id: "1",
    createAt: "2024-01-15T10:30:00Z",
    members: [
      {
        id: "user1",
        email: "john@example.com",
        username: "john_doe",
        fullname: "John Doe",
        gender: "MALE",
        dob: "1990-01-15",
        createdAt: "2023-01-15T00:00:00Z",
        privacySetting: "PUBLIC",
        role: "USER",
      },
      {
        id: "user2",
        email: "jane@example.com",
        username: "jane_smith",
        fullname: "Jane Smith",
        gender: "FEMALE",
        dob: "1992-03-20",
        createdAt: "2023-02-10T00:00:00Z",
        privacySetting: "PUBLIC",
        role: "USER",
      },
    ],
    messages: [],
  },
]

const mockMessages: MessageResponse[] = [
  {
    id: "1",
    sender: {
      id: "user2",
      email: "jane@example.com",
      username: "jane_smith",
      fullname: "Jane Smith",
      gender: "FEMALE",
      dob: "1992-03-20",
      createdAt: "2023-02-10T00:00:00Z",
      privacySetting: "PUBLIC",
      role: "USER",
    },
    chatId: "1",
    sendAt: "2024-01-15T10:30:00Z",
    content: "Hey, how are you doing?",
  },
  {
    id: "2",
    sender: {
      id: "user1",
      email: "john@example.com",
      username: "john_doe",
      fullname: "John Doe",
      gender: "MALE",
      dob: "1990-01-15",
      createdAt: "2023-01-15T00:00:00Z",
      privacySetting: "PUBLIC",
      role: "USER",
    },
    chatId: "1",
    sendAt: "2024-01-15T10:32:00Z",
    content: "I'm doing great! Just working on some new features.",
  },
]

export default function ChatPage() {
  const [selectedChat, setSelectedChat] = useState<ChatResponse>(mockChats[0])
  const [messages, setMessages] = useState<MessageResponse[]>(mockMessages)
  const [newMessage, setNewMessage] = useState("")
  const [searchQuery, setSearchQuery] = useState("")
  const messagesEndRef = useRef<HTMLDivElement>(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  useEffect(() => {
    // Load messages for selected chat
    if (selectedChat) {
      loadChatMessages(selectedChat.id)
    }
  }, [selectedChat])

  const loadChatMessages = async (chatId: string) => {
    try {
      const response = await apiClient.getChatMessages(chatId)
      if (response.code === "200") {
        setMessages(response.result)
      }
    } catch (error) {
      console.error("Error loading messages:", error)
      // Use mock data for demo
      setMessages(mockMessages)
    }
  }

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault()
    if (!newMessage.trim()) return

    const messageRequest: MessageCreateRequest = {
      senderId: "user1", // Get from auth context
      chatId: selectedChat.id,
      content: newMessage,
    }

    // Send via WebSocket
    wsClient.sendMessage(messageRequest)

    // Add to local state for immediate UI update
    const message: MessageResponse = {
      id: Date.now().toString(),
      sender: {
        id: "user1",
        email: "john@example.com",
        username: "john_doe",
        fullname: "John Doe",
        gender: "MALE",
        dob: "1990-01-15",
        createdAt: "2023-01-15T00:00:00Z",
        privacySetting: "PUBLIC",
        role: "USER",
      },
      chatId: selectedChat.id,
      sendAt: new Date().toISOString(),
      content: newMessage,
    }

    setMessages([...messages, message])
    setNewMessage("")
  }

  const getChatDisplayInfo = (chat: ChatResponse) => {
    // For private chats, show the other member's info
    const otherMember = chat.members.find((member) => member.id !== "user1")
    if (otherMember) {
      return {
        name: otherMember.fullname,
        avatar:
          otherMember.avatar && otherMember.avatarType
            ? byteArrayToImageUrl(otherMember.avatar, otherMember.avatarType)
            : "/placeholder.svg?height=40&width=40",
        isOnline: true, // Mock online status
        type: "PRIVATE" as const,
      }
    }

    // For group chats
    return {
      name: `Group Chat (${chat.members.length} members)`,
      avatar: "/placeholder.svg?height=40&width=40",
      isOnline: false,
      type: "GROUP" as const,
      memberCount: chat.members.length,
    }
  }

  const filteredChats = mockChats.filter((chat) => {
    const displayInfo = getChatDisplayInfo(chat)
    return displayInfo.name.toLowerCase().includes(searchQuery.toLowerCase())
  })

  const selectedChatInfo = getChatDisplayInfo(selectedChat)

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 h-[calc(100vh-8rem)]">
          {/* Chat List */}
          <Card className="lg:col-span-1">
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle>Messages</CardTitle>
                <Button size="sm" variant="outline">
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                <Input
                  placeholder="Search conversations..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </CardHeader>
            <CardContent className="p-0">
              <div className="space-y-1">
                {filteredChats.map((chat) => {
                  const chatInfo = getChatDisplayInfo(chat)
                  const lastMessage = chat.messages[chat.messages.length - 1]

                  return (
                    <div
                      key={chat.id}
                      className={`flex items-center gap-3 p-3 cursor-pointer hover:bg-gray-50 ${
                        selectedChat.id === chat.id ? "bg-blue-50 border-r-2 border-blue-500" : ""
                      }`}
                      onClick={() => setSelectedChat(chat)}
                    >
                      <div className="relative">
                        <Avatar>
                          <AvatarImage src={chatInfo.avatar || "/placeholder.svg"} alt={chatInfo.name} />
                          <AvatarFallback>
                            {chatInfo.name
                              .split(" ")
                              .map((n) => n[0])
                              .join("")}
                          </AvatarFallback>
                        </Avatar>
                        {chatInfo.isOnline && (
                          <div className="absolute bottom-0 right-0 h-3 w-3 bg-green-500 border-2 border-white rounded-full"></div>
                        )}
                      </div>

                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between">
                          <h4 className="font-medium truncate">{chatInfo.name}</h4>
                          <span className="text-xs text-gray-500">
                            {lastMessage &&
                              new Date(lastMessage.sendAt).toLocaleTimeString("en-US", {
                                hour: "2-digit",
                                minute: "2-digit",
                              })}
                          </span>
                        </div>
                        <div className="flex items-center justify-between">
                          <p className="text-sm text-gray-600 truncate">{lastMessage?.content || "No messages yet"}</p>
                        </div>
                        {chatInfo.type === "GROUP" && (
                          <p className="text-xs text-gray-500">{chatInfo.memberCount} members</p>
                        )}
                      </div>
                    </div>
                  )
                })}
              </div>
            </CardContent>
          </Card>

          {/* Chat Window */}
          <Card className="lg:col-span-3 flex flex-col">
            {/* Chat Header */}
            <CardHeader className="border-b">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Avatar>
                    <AvatarImage src={selectedChatInfo.avatar || "/placeholder.svg"} alt={selectedChatInfo.name} />
                    <AvatarFallback>
                      {selectedChatInfo.name
                        .split(" ")
                        .map((n) => n[0])
                        .join("")}
                    </AvatarFallback>
                  </Avatar>
                  <div>
                    <h3 className="font-semibold">{selectedChatInfo.name}</h3>
                    <p className="text-sm text-gray-600">
                      {selectedChatInfo.type === "GROUP"
                        ? `${selectedChatInfo.memberCount} members`
                        : selectedChatInfo.isOnline
                          ? "Online"
                          : "Offline"}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <Button variant="ghost" size="sm">
                    <Phone className="h-4 w-4" />
                  </Button>
                  <Button variant="ghost" size="sm">
                    <Video className="h-4 w-4" />
                  </Button>
                  <Button variant="ghost" size="sm">
                    <MoreVertical className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </CardHeader>

            {/* Messages */}
            <CardContent className="flex-1 overflow-y-auto p-4 space-y-4">
              {messages.map((message) => {
                const isCurrentUser = message.sender.id === "user1"
                const senderAvatar =
                  message.sender.avatar && message.sender.avatarType
                    ? byteArrayToImageUrl(message.sender.avatar, message.sender.avatarType)
                    : "/placeholder.svg?height=32&width=32"

                return (
                  <div key={message.id} className={`flex ${isCurrentUser ? "justify-end" : "justify-start"}`}>
                    {!isCurrentUser && (
                      <Avatar className="h-8 w-8 mr-2">
                        <AvatarImage src={senderAvatar || "/placeholder.svg"} alt={message.sender.fullname} />
                        <AvatarFallback>
                          {message.sender.fullname
                            .split(" ")
                            .map((n) => n[0])
                            .join("")}
                        </AvatarFallback>
                      </Avatar>
                    )}
                    <div
                      className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                        isCurrentUser ? "bg-blue-600 text-white" : "bg-gray-200 text-gray-900"
                      }`}
                    >
                      {!isCurrentUser && <p className="text-xs font-semibold mb-1">{message.sender.fullname}</p>}
                      <p>{message.content}</p>
                      <p className={`text-xs mt-1 ${isCurrentUser ? "text-blue-100" : "text-gray-500"}`}>
                        {new Date(message.sendAt).toLocaleTimeString("en-US", {
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </p>
                    </div>
                  </div>
                )
              })}
              <div ref={messagesEndRef} />
            </CardContent>

            {/* Message Input */}
            <div className="border-t p-4">
              <form onSubmit={handleSendMessage} className="flex gap-2">
                <Input
                  placeholder="Type a message..."
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  className="flex-1"
                />
                <Button type="submit" disabled={!newMessage.trim()}>
                  <Send className="h-4 w-4" />
                </Button>
              </form>
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}
