const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1"

// Response interfaces matching backend models
interface ApiResponse<T> {
  code: string
  result: T
}

interface JwtAuthenticationResponse {
  accessToken: string
  tokenType: string
}

interface UserResponse {
  id: string
  email: string
  username: string
  fullname: string
  bio?: string
  gender: string
  dob: string // Date as ISO string
  createdAt: string
  privacySetting: "PUBLIC" | "FRIENDS" | "PRIVATE"
  avatar?: number[] // byte array
  avatarType?: string
  coverPicture?: number[]
  coverPictureType?: string
  role: "USER" | "ADMIN" | "MODERATOR"
}

interface PostResponse {
  id: string
  content: string
  imageData?: number[]
  imageType?: string
  createAt: string // LocalDateTime as ISO string
  privacySetting: "PUBLIC" | "FRIENDS" | "PRIVATE"
  author: UserResponse
}

interface CommentResponse {
  id: string
  content: string
  imageData?: number[]
  imageType?: string
  createAt: string
  author: UserResponse
  postId: string
}

interface PostReactionResponse {
  id: string
  user: UserResponse
  postId: string
  reactionType: "LIKE" | "LOVE" | "HAHA" | "WOW" | "SAD" | "ANGRY"
  createAt: string
}

interface CommentReactionResponse {
  id: string
  user: UserResponse
  commentId: string
  reactionType: "LIKE" | "LOVE" | "HAHA" | "WOW" | "SAD" | "ANGRY"
  createAt: string
}

interface MessageResponse {
  id: string
  sender: UserResponse
  chatId: string
  sendAt: string
  content: string
}

interface ChatResponse {
  id: string
  createAt: string
  members: UserResponse[]
  messages: MessageResponse[]
}

// Request interfaces
interface LoginRequest {
  usernameOrEmail: string
  password: string
}

interface UserCreateRequest {
  email: string
  username: string
  fullname: string
  password: string
  gender: string
  dob: string // ISO date string
}

interface UserUpdateRequest {
  id: string
  username: string
  fullname: string
  password?: string
  bio?: string
  gender: string
  dob: string
  privacySetting: "PUBLIC" | "FRIENDS" | "PRIVATE"
  avatar?: number[] // byte array as number array
  avatarType?: string
  coverPicture?: number[]
  coverPictureType?: string
  role: "USER" | "ADMIN" | "MODERATOR"
}

interface PostReactionCreateRequest {
  userId: string
  postId: string
  reactionType: "LIKE" | "LOVE" | "HAHA" | "WOW" | "SAD" | "ANGRY"
}

interface PostReactionUpdateRequest {
  id: string
  userId: string
  postId: string
  reactionType: "LIKE" | "LOVE" | "HAHA" | "WOW" | "SAD" | "ANGRY"
}

interface ChatCreateRequest {
  memberIds: string[]
}

interface CommentCreateRequest {
  authorId: string
  postId: string
  content: string
  imageData?: number[] // byte array as number array in JS
  imageType?: string
}

interface CommentUpdateRequest {
  id: string
  authorId: string
  postId: string
  content: string
  imageData?: number[]
  imageType?: string
}

interface CommentReactionCreateRequest {
  userId: string
  commentId: string
  reactionType: "LIKE" | "LOVE" | "HAHA" | "WOW" | "SAD" | "ANGRY"
}

interface MessageCreateRequest {
  senderId: string
  chatId: string
  content: string
}

interface MessageUpdateRequest {
  id: string
  senderId: string
  chatId: string
  content: string
}

// Utility function to convert byte array to blob URL
function byteArrayToImageUrl(byteArray: number[], mimeType: string): string {
  if (!byteArray || byteArray.length === 0) return "/placeholder.svg"

  const uint8Array = new Uint8Array(byteArray)
  const blob = new Blob([uint8Array], { type: mimeType })
  return URL.createObjectURL(blob)
}

class ApiClient {
  private getAuthHeaders() {
    const token = localStorage.getItem("token")
    return {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
    }
  }

  // Auth endpoints
  async login(credentials: LoginRequest): Promise<ApiResponse<JwtAuthenticationResponse>> {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(credentials),
    })
    return response.json()
  }

  // User endpoints
  async getUser(userId: string): Promise<ApiResponse<UserResponse>> {
    const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getUserByEmail(email: string): Promise<ApiResponse<UserResponse>> {
    const response = await fetch(`${API_BASE_URL}/users/email/${email}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getUserByUsername(username: string): Promise<ApiResponse<UserResponse>> {
    const response = await fetch(`${API_BASE_URL}/users/username/${username}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getAllUsers(): Promise<ApiResponse<UserResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/users`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async createUser(userData: UserCreateRequest): Promise<ApiResponse<UserResponse>> {
    const response = await fetch(`${API_BASE_URL}/users`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userData),
    })
    return response.json()
  }

  async updateUser(userId: string, userData: UserUpdateRequest): Promise<ApiResponse<UserResponse>> {
    const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
      method: "PUT",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(userData),
    })
    return response.json()
  }

  async deleteUser(userId: string): Promise<ApiResponse<string>> {
    const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  // Post endpoints
  async getAllPosts(): Promise<ApiResponse<PostResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/posts`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getPost(postId: string): Promise<ApiResponse<PostResponse>> {
    const response = await fetch(`${API_BASE_URL}/posts/${postId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getPostsByAuthor(authorId: string): Promise<ApiResponse<PostResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/posts/author/${authorId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getPostsByPrivacy(privacySetting: "PUBLIC" | "FRIENDS" | "PRIVATE"): Promise<ApiResponse<PostResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/posts/privacy/${privacySetting}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async createPost(postData: any): Promise<ApiResponse<PostResponse>> {
    const response = await fetch(`${API_BASE_URL}/posts`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(postData),
    })
    return response.json()
  }

  async updatePost(postId: string, postData: any): Promise<ApiResponse<PostResponse>> {
    const response = await fetch(`${API_BASE_URL}/posts/${postId}`, {
      method: "PUT",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(postData),
    })
    return response.json()
  }

  async deletePost(postId: string): Promise<ApiResponse<string>> {
    const response = await fetch(`${API_BASE_URL}/posts/${postId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  // Comment endpoints
  async getComment(commentId: string): Promise<ApiResponse<CommentResponse>> {
    const response = await fetch(`${API_BASE_URL}/comments/${commentId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getCommentsByPost(postId: string): Promise<ApiResponse<CommentResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/posts/${postId}/comments`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getCommentsByAuthor(authorId: string): Promise<ApiResponse<CommentResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/users/${authorId}/comments`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async createComment(commentData: CommentCreateRequest): Promise<ApiResponse<CommentResponse>> {
    const response = await fetch(`${API_BASE_URL}/comments`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(commentData),
    })
    return response.json()
  }

  async updateComment(commentId: string, commentData: CommentUpdateRequest): Promise<ApiResponse<CommentResponse>> {
    const response = await fetch(`${API_BASE_URL}/comments/${commentId}`, {
      method: "PUT",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(commentData),
    })
    return response.json()
  }

  async deleteComment(commentId: string): Promise<ApiResponse<string>> {
    const response = await fetch(`${API_BASE_URL}/comments/${commentId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  // Post Reaction endpoints
  async createOrUpdatePostReaction(
    reactionData: PostReactionCreateRequest,
  ): Promise<ApiResponse<PostReactionResponse>> {
    const response = await fetch(`${API_BASE_URL}/reactions`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(reactionData),
    })
    return response.json()
  }

  async deletePostReaction(userId: string, postId: string): Promise<ApiResponse<string>> {
    const response = await fetch(`${API_BASE_URL}/users/${userId}/reactions/posts/${postId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getPostReactions(postId: string): Promise<ApiResponse<PostReactionResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/posts/${postId}/reactions`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getUserPostReactions(userId: string): Promise<ApiResponse<PostReactionResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/users/${userId}/reactions`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  // Comment Reaction endpoints
  async createOrUpdateCommentReaction(
    reactionData: CommentReactionCreateRequest,
  ): Promise<ApiResponse<CommentReactionResponse>> {
    const response = await fetch(`${API_BASE_URL}/comment-reactions`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(reactionData),
    })
    return response.json()
  }

  async deleteCommentReaction(userId: string, commentId: string): Promise<ApiResponse<string>> {
    const response = await fetch(`${API_BASE_URL}/users/${userId}/comment-reactions/comments/${commentId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getCommentReactions(commentId: string): Promise<ApiResponse<CommentReactionResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/comments/${commentId}/reactions`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getUserCommentReactions(userId: string): Promise<ApiResponse<CommentReactionResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/users/${userId}/comment-reactions`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  // Chat endpoints
  async getUserChats(userId: string): Promise<ApiResponse<ChatResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/chats/user/${userId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getChat(chatId: string): Promise<ApiResponse<ChatResponse>> {
    const response = await fetch(`${API_BASE_URL}/chats/${chatId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async createChat(chatData: ChatCreateRequest): Promise<ApiResponse<ChatResponse>> {
    const response = await fetch(`${API_BASE_URL}/chats`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(chatData),
    })
    return response.json()
  }

  async addMemberToChat(chatId: string, userId: string): Promise<ApiResponse<ChatResponse>> {
    const response = await fetch(`${API_BASE_URL}/chats/${chatId}/members/${userId}`, {
      method: "PUT",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async removeMemberFromChat(chatId: string, userId: string): Promise<ApiResponse<ChatResponse>> {
    const response = await fetch(`${API_BASE_URL}/chats/${chatId}/members/${userId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async deleteChat(chatId: string): Promise<ApiResponse<string>> {
    const response = await fetch(`${API_BASE_URL}/chats/${chatId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  // Message endpoints
  async getChatMessages(chatId: string): Promise<ApiResponse<MessageResponse[]>> {
    const response = await fetch(`${API_BASE_URL}/messages/chat/${chatId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async getMessage(messageId: string): Promise<ApiResponse<MessageResponse>> {
    const response = await fetch(`${API_BASE_URL}/messages/${messageId}`, {
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }

  async updateMessage(messageId: string, messageData: MessageUpdateRequest): Promise<ApiResponse<MessageResponse>> {
    const response = await fetch(`${API_BASE_URL}/messages/${messageId}`, {
      method: "PUT",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(messageData),
    })
    return response.json()
  }

  async deleteMessage(messageId: string): Promise<ApiResponse<string>> {
    const response = await fetch(`${API_BASE_URL}/messages/${messageId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders(),
    })
    return response.json()
  }
}

export const apiClient = new ApiClient()

// Export utility function
export { byteArrayToImageUrl }

// Export types for use in components
export type {
  ApiResponse,
  JwtAuthenticationResponse,
  UserResponse,
  PostResponse,
  CommentResponse,
  PostReactionResponse,
  CommentReactionResponse,
  MessageResponse,
  ChatResponse,
  LoginRequest,
  UserCreateRequest,
  UserUpdateRequest,
  PostReactionCreateRequest,
  PostReactionUpdateRequest,
  ChatCreateRequest,
  CommentCreateRequest,
  CommentUpdateRequest,
  CommentReactionCreateRequest,
  MessageCreateRequest,
  MessageUpdateRequest,
}
