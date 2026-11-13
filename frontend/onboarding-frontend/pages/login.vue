<template>
  <div style="max-width: 400px; margin: 80px auto; padding: 30px; border: 1px solid #ddd; border-radius: 8px; font-family: Arial, sans-serif;">
    <h2>Keycloak Login</h2>
    <form @submit.prevent="handleLogin" style="margin-top: 20px;">
      <div style="margin-bottom: 15px;">
        <label>Username:</label><br>
        <input v-model="username" type="text" style="width: 100%; padding: 8px; margin-top: 5px;" required />
      </div>
      <div style="margin-bottom: 15px;">
        <label>Password:</label><br>
        <input v-model="password" type="password" style="width: 100%; padding: 8px; margin-top: 5px;" required />
      </div>
      <button type="submit" style="width: 100%; padding: 10px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
        Login
      </button>
    </form>
    <p v-if="errorMessage" style="color: red; margin-top: 15px; font-size: 14px;">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
const username = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

const handleLogin = async () => {
  try {
    errorMessage.value = ''
    
    // Keycloak token endpoint parameters
    const params = new URLSearchParams()
    params.append('client_id', 'onboarding-client')
    params.append('username', username.value)
    params.append('password', password.value)
    params.append('grant_type', 'password')

    // Sending POST request directly to Keycloak to get the token with form-urlencoded header
    const response = await $fetch('http://localhost:8180/realms/my-enterprise-realm/protocol/openid-connect/token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: params
    })

    if (response.access_token) {
      // Save token to localStorage
      localStorage.setItem('keycloak_token', response.access_token)
      alert('Login Successful!')
      router.push('/') // Redirect back to Dashboard
    }
  } catch (error) {
    errorMessage.value = 'Invalid username or password!'
    console.error(error)
  }
}
</script>