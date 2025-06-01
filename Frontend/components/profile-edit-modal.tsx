"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Camera, Upload } from "lucide-react"
import { apiClient } from "@/lib/api"
import type { UserUpdateRequest } from "@/lib/api"

interface ProfileEditModalProps {
  user: {
    id: string
    username: string
    fullName: string
    email: string
    bio?: string
    gender: string
    dob: string
    avatar: string
    coverImage?: string
    privacySetting: string
  }
  onUserUpdated: (updatedUser: any) => void
  children: React.ReactNode
}

export function ProfileEditModal({ user, onUserUpdated, children }: ProfileEditModalProps) {
  const [isOpen, setIsOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [formData, setFormData] = useState({
    username: user.username,
    fullname: user.fullName,
    bio: user.bio || "",
    gender: user.gender,
    dob: user.dob,
    privacySetting: user.privacySetting,
  })
  const [avatarFile, setAvatarFile] = useState<File | null>(null)
  const [coverFile, setCoverFile] = useState<File | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      // Convert files to byte arrays if needed
      let avatarData: number[] | undefined
      let avatarType: string | undefined
      let coverData: number[] | undefined
      let coverType: string | undefined

      if (avatarFile) {
        const arrayBuffer = await avatarFile.arrayBuffer()
        avatarData = Array.from(new Uint8Array(arrayBuffer))
        avatarType = avatarFile.type
      }

      if (coverFile) {
        const arrayBuffer = await coverFile.arrayBuffer()
        coverData = Array.from(new Uint8Array(arrayBuffer))
        coverType = coverFile.type
      }

      const updateRequest: UserUpdateRequest = {
        id: user.id,
        username: formData.username,
        fullname: formData.fullname,
        bio: formData.bio,
        gender: formData.gender,
        dob: formData.dob,
        privacySetting: formData.privacySetting as "PUBLIC" | "FRIENDS" | "PRIVATE",
        avatar: avatarData,
        avatarType,
        coverPicture: coverData,
        coverPictureType: coverType,
        role: "USER", // Default role
      }

      const response = await apiClient.updateUser(user.id, updateRequest)

      if (response.code === "200") {
        onUserUpdated(response.result)
        setIsOpen(false)
      }
    } catch (error) {
      console.error("Error updating profile:", error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setAvatarFile(file)
    }
  }

  const handleCoverChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setCoverFile(file)
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-[600px] max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Edit Profile</DialogTitle>
          <DialogDescription>Make changes to your profile information.</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Avatar Upload */}
          <div className="flex items-center gap-4">
            <div className="relative">
              <Avatar className="h-20 w-20">
                <AvatarImage src={avatarFile ? URL.createObjectURL(avatarFile) : user.avatar} alt={user.fullName} />
                <AvatarFallback>
                  {user.fullName
                    .split(" ")
                    .map((n) => n[0])
                    .join("")}
                </AvatarFallback>
              </Avatar>
              <label
                htmlFor="avatar-upload"
                className="absolute bottom-0 right-0 bg-blue-600 text-white p-1 rounded-full cursor-pointer hover:bg-blue-700"
              >
                <Camera className="h-3 w-3" />
              </label>
              <input id="avatar-upload" type="file" accept="image/*" onChange={handleAvatarChange} className="hidden" />
            </div>
            <div>
              <h3 className="font-medium">Profile Picture</h3>
              <p className="text-sm text-gray-600">Click the camera icon to change your avatar</p>
            </div>
          </div>

          {/* Cover Photo Upload */}
          <div className="space-y-2">
            <Label>Cover Photo</Label>
            <div className="border-2 border-dashed border-gray-300 rounded-lg p-4">
              <label htmlFor="cover-upload" className="cursor-pointer">
                <div className="flex flex-col items-center gap-2">
                  <Upload className="h-8 w-8 text-gray-400" />
                  <span className="text-sm text-gray-600">
                    {coverFile ? coverFile.name : "Click to upload cover photo"}
                  </span>
                </div>
              </label>
              <input id="cover-upload" type="file" accept="image/*" onChange={handleCoverChange} className="hidden" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="fullname">Full Name</Label>
              <Input
                id="fullname"
                value={formData.fullname}
                onChange={(e) => setFormData({ ...formData, fullname: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="bio">Bio</Label>
            <Textarea
              id="bio"
              placeholder="Tell us about yourself..."
              value={formData.bio}
              onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
              rows={3}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="gender">Gender</Label>
              <Select value={formData.gender} onValueChange={(value) => setFormData({ ...formData, gender: value })}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="MALE">Male</SelectItem>
                  <SelectItem value="FEMALE">Female</SelectItem>
                  <SelectItem value="OTHER">Other</SelectItem>
                  <SelectItem value="PREFER_NOT_TO_SAY">Prefer not to say</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="dob">Date of Birth</Label>
              <Input
                id="dob"
                type="date"
                value={formData.dob}
                onChange={(e) => setFormData({ ...formData, dob: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="privacy">Privacy Setting</Label>
            <Select
              value={formData.privacySetting}
              onValueChange={(value) => setFormData({ ...formData, privacySetting: value })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PUBLIC">Public</SelectItem>
                <SelectItem value="FRIENDS">Friends Only</SelectItem>
                <SelectItem value="PRIVATE">Private</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? "Saving..." : "Save Changes"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
