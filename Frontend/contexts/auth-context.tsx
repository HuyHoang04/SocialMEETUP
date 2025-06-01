"use client"

import type React from "react"

import { createContext, useContext, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { apiClient } from "@/lib/api"
import { wsClient } from "@/lib/websocket"
import type { LoginRequest, UserResponse } from "@/lib/api"

interface AuthContextType {
  user: UserResponse | null
  isLoading: boolean
  login: (credentials: { usernameOrEmail: string; password: string }) => Promise<void>
  logout: () => void
  updateUser: (userData: Partial<UserResponse>) => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()

  useEffect(() => {
    const token = localStorage.getItem("token")
    if (token) {
      // In real app: verify token and get user data
      // For demo, set mock user with proper UserResponse structure
      setUser({
        id: "user1",
        email: "john@example.com",
        username: "john_doe",
        fullname: "John Doe",
        bio: "Software Developer | Tech Enthusiast",
        gender: "MALE",
        dob: "1990-01-15",
        createdAt: "2023-01-15T00:00:00Z",
        privacySetting: "PUBLIC",
        role: "USER",
      })

      // Connect to WebSocket
      wsClient.connect(token)
    }
    setIsLoading(false)
  }, [])

  const login = async (credentials: { usernameOrEmail: string; password: string }) => {
    try {
      const loginRequest: LoginRequest = {
        usernameOrEmail: credentials.usernameOrEmail,
        password: credentials.password,
      }

      const response = await apiClient.login(loginRequest)

      if (response.code === "200") {
        const token = response.result.accessToken
        localStorage.setItem("token", token)

        // Get user data (in real app)
        // const userData = await apiClient.getUser(userId)
        // setUser(userData.result)

        // For demo
        setUser({
          id: "user1",
          email: "john@example.com",
          username: "john_doe",
          fullname: "John Doe",
          bio: "Software Developer | Tech Enthusiast",
          gender: "MALE",
          dob: "1990-01-15",
          createdAt: "2023-01-15T00:00:00Z",
          privacySetting: "PUBLIC",
          role: "USER",
        })

        wsClient.connect(token)
        router.push("/")
      } else {
        throw new Error("Login failed")
      }
    } catch (error) {
      throw new Error("Login failed")
    }
  }

  const logout = () => {
    localStorage.removeItem("token")
    setUser(null)
    wsClient.disconnect()
    router.push("/login")
  }

  const updateUser = (userData: Partial<UserResponse>) => {
    if (user) {
      setUser({ ...user, ...userData })
    }
  }

  return <AuthContext.Provider value={{ user, isLoading, login, logout, updateUser }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
