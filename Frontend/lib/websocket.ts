import type { MessageCreateRequest } from "./api"

class WebSocketClient {
  private ws: WebSocket | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectInterval = 1000

  connect(token: string) {
    const wsUrl = process.env.NEXT_PUBLIC_WS_URL || "ws://localhost:8080/ws"

    try {
      this.ws = new WebSocket(`${wsUrl}?token=${token}`)

      this.ws.onopen = () => {
        console.log("WebSocket connected")
        this.reconnectAttempts = 0
      }

      this.ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          this.handleMessage(message)
        } catch (error) {
          console.error("Error parsing WebSocket message:", error)
        }
      }

      this.ws.onclose = () => {
        console.log("WebSocket disconnected")
        this.attemptReconnect(token)
      }

      this.ws.onerror = (error) => {
        console.error("WebSocket error:", error)
      }
    } catch (error) {
      console.error("Error connecting to WebSocket:", error)
    }
  }

  private attemptReconnect(token: string) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      setTimeout(() => {
        console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
        this.connect(token)
      }, this.reconnectInterval * this.reconnectAttempts)
    }
  }

  private handleMessage(message: any) {
    // Handle different types of WebSocket messages
    switch (message.type) {
      case "NEW_MESSAGE":
        // Dispatch event for new message
        window.dispatchEvent(new CustomEvent("newMessage", { detail: message.data }))
        break
      case "MESSAGE_UPDATED":
        window.dispatchEvent(new CustomEvent("messageUpdated", { detail: message.data }))
        break
      case "MESSAGE_DELETED":
        window.dispatchEvent(new CustomEvent("messageDeleted", { detail: message.data }))
        break
      case "USER_TYPING":
        window.dispatchEvent(new CustomEvent("userTyping", { detail: message.data }))
        break
      default:
        console.log("Unknown message type:", message.type)
    }
  }

  // Send message using the correct MessageCreateRequest structure
  sendMessage(messageRequest: MessageCreateRequest) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      // Send to WebSocket endpoint: /app/chat/{chatId}
      const wsMessage = {
        destination: `/app/chat/${messageRequest.chatId}`,
        body: JSON.stringify(messageRequest),
      }
      this.ws.send(JSON.stringify(wsMessage))
    } else {
      console.error("WebSocket is not connected")
    }
  }

  sendTypingIndicator(chatId: string, isTyping: boolean) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message = {
        type: "TYPING_INDICATOR",
        chatId,
        isTyping,
        timestamp: new Date().toISOString(),
      }
      this.ws.send(JSON.stringify(message))
    }
  }

  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }
}

export const wsClient = new WebSocketClient()
