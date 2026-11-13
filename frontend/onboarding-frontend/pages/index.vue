<template>
  <div style="padding: 40px; font-family: Arial, sans-serif; max-width: 1000px; margin: 0 auto;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
      <h1> Onboarding Dashboard (Day 27: Filter & Pagination)</h1>
      <button @click="logout" style="padding: 8px 15px; background: #dc3545; color: white; border: none; border-radius: 4px; cursor: pointer;">
        Logout
      </button>
    </div>

    <!-- Global Error Banner -->
    <div v-if="errorMessage" style="background: #f8d7da; color: #721c24; padding: 12px; border-radius: 6px; margin-bottom: 20px; border: 1px solid #f5c6cb;">
      <strong>Error:</strong> {{ errorMessage }}
    </div>

    <div style="display: flex; gap: 30px;">
      
      <!-- ================= PROJECTS SECTION ================= -->
      <div style="flex: 1; background: #f9f9f9; padding: 20px; border-radius: 8px; border: 1px solid #ddd;">
        <h2>Projects (Page {{ projectPage + 1 }})</h2>
        
        <!-- Create Project Form -->
        <form @submit.prevent="createProject" style="margin-bottom: 20px; background: #fff; padding: 15px; border-radius: 6px; border: 1px solid #eee;">
          <h4>Add New Project</h4>
          <div style="margin-bottom: 10px;">
            <input v-model="newProject.name" type="text" placeholder="Project Name" style="width: 100%; padding: 6px;" required />
          </div>
          <div style="margin-bottom: 10px;">
            <input v-model="newProject.description" type="text" placeholder="Description" style="width: 100%; padding: 6px;" />
          </div>
          <button type="submit" style="padding: 6px 12px; background: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer;">
            Create Project
          </button>
        </form>

        <!-- Projects List -->
        <div v-if="projects.length === 0" style="color: #666;">No projects found.</div>
        <ul v-else style="padding-left: 20px;">
          <li v-for="proj in projects" :key="proj.id" style="margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center;">
            <div>
              <strong>{{ proj.name }}</strong><br>
              <small style="color: #555;">{{ proj.description }}</small>
            </div>
            <button @click="deleteProject(proj.id)" style="background: #ff4d4d; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 12px;">
              Delete
            </button>
          </li>
        </ul>

        <!-- Project Pagination Controls -->
        <div style="display: flex; justify-content: space-between; margin-top: 15px;">
          <button @click="prevProjectPage" :disabled="projectPage === 0" style="padding: 4px 10px; cursor: pointer;">Previous</button>
          <span>Page {{ projectPage + 1 }} of {{ totalProjectPages || 1 }}</span>
          <button @click="nextProjectPage" :disabled="projectPage >= totalProjectPages - 1" style="padding: 4px 10px; cursor: pointer;">Next</button>
        </div>
      </div>

      <!-- ================= TASKS SECTION ================= -->
      <div style="flex: 1; background: #f9f9f9; padding: 20px; border-radius: 8px; border: 1px solid #ddd;">
        <h2>Tasks</h2>
        
        <!-- Filter Controls for Tasks -->
        <div style="margin-bottom: 15px; background: #fff; padding: 10px; border-radius: 6px; border: 1px solid #eee;">
          <label style="font-size: 14px; font-weight: bold; margin-right: 10px;">Filter Status:</label>
          <select v-model="taskFilter" @change="fetchData" style="padding: 4px;">
            <option value="">All Tasks</option>
            <option value="false">Pending</option>
            <option value="true">Completed</option>
          </select>
        </div>

        <!-- Create Task Form -->
        <form @submit.prevent="createTask" style="margin-bottom: 20px; background: #fff; padding: 15px; border-radius: 6px; border: 1px solid #eee;">
          <h4>Add New Task</h4>
          <div style="margin-bottom: 10px;">
            <input v-model="newTask.title" type="text" placeholder="Task Title (min 3 chars)" style="width: 100%; padding: 6px;" required />
          </div>
          <div style="margin-bottom: 10px;">
            <input v-model="newTask.description" type="text" placeholder="Task Description" style="width: 100%; padding: 6px;" />
          </div>
          <div style="margin-bottom: 10px;">
            <select v-model="newTask.projectId" style="width: 100%; padding: 6px;" required>
              <option disabled value="">Select Project</option>
              <option v-for="proj in allProjectsList" :key="proj.id" :value="proj.id">
                {{ proj.name }}
              </option>
            </select>
          </div>
          <button type="submit" style="padding: 6px 12px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
            Create Task
          </button>
        </form>

        <!-- Tasks List -->
        <div v-if="tasks.length === 0" style="color: #666;">No tasks found.</div>
        <ul v-else style="padding-left: 20px;">
          <li v-for="task in tasks" :key="task.id" style="margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center;">
            <div>
              <strong>{{ task.title }}</strong><br>
              <span style="font-size: 12px; color: #555;">Status: {{ task.completed ? 'Completed' : 'Pending' }}</span>
            </div>
            <button @click="deleteTask(task.id)" style="background: #ff4d4d; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 12px;">
              Delete
            </button>
          </li>
        </ul>

        <!-- Task Pagination Controls -->
        <div style="display: flex; justify-content: space-between; margin-top: 15px;">
          <button @click="prevTaskPage" :disabled="taskPage === 0" style="padding: 4px 10px; cursor: pointer;">Previous</button>
          <span>Page {{ taskPage + 1 }} of {{ totalTaskPages || 1 }}</span>
          <button @click="nextTaskPage" :disabled="taskPage >= totalTaskPages - 1" style="padding: 4px 10px; cursor: pointer;">Next</button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
const projects = ref([])
const allProjectsList = ref([]) // For dropdown selection
const tasks = ref([])
const errorMessage = ref('')
const router = useRouter()

// Pagination & Filter State
const projectPage = ref(0)
const totalProjectPages = ref(0)
const taskPage = ref(0)
const totalTaskPages = ref(0)
const taskFilter = ref('') // '' = all, 'true' = completed, 'false' = pending

const newProject = ref({ name: '', description: '' })
const newTask = ref({ title: '', description: '', projectId: '' })

const getToken = () => localStorage.getItem('keycloak_token')

// Fetch Data with Pagination and Filter Parameters
const fetchData = async () => {
  const token = getToken()
  if (!token) {
    router.push('/login')
    return
  }

  try {
    errorMessage.value = ''
    
    // 1. Fetch Projects with Page parameter
    const projectsRes = await $fetch(`http://localhost:8080/api/projects?page=${projectPage.value}&size=5`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    projects.value = projectsRes.content || []
    totalProjectPages.value = projectsRes.totalPages || 0

    // Fetch all projects without pagination for task dropdown
    const allProjRes = await $fetch(`http://localhost:8080/api/projects?size=100`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    allProjectsList.value = allProjRes.content || allProjRes || []

    // 2. Fetch Tasks with Page and Optional Completed Filter
    let taskUrl = `http://localhost:8080/api/tasks?page=${taskPage.value}&size=5`
    if (taskFilter.value !== '') {
      taskUrl += `&completed=${taskFilter.value}`
    }

    const tasksRes = await $fetch(taskUrl, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    tasks.value = tasksRes.content || []
    totalTaskPages.value = tasksRes.totalPages || 0

  } catch (error) {
    console.error('Failed to load data:', error)
    if (error.response && error.response.status === 401) {
      alert('Session expired. Please login again.')
      localStorage.removeItem('keycloak_token')
      router.push('/login')
    } else {
      errorMessage.value = 'Failed to connect to backend server.'
    }
  }
}

onMounted(() => {
  fetchData()
})

// Pagination Navigation Handlers
const nextProjectPage = () => {
  if (projectPage.value < totalProjectPages.value - 1) {
    projectPage.value++
    fetchData()
  }
}

const prevProjectPage = () => {
  if (projectPage.value > 0) {
    projectPage.value--
    fetchData()
  }
}

const nextTaskPage = () => {
  if (taskPage.value < totalTaskPages.value - 1) {
    taskPage.value++
    fetchData()
  }
}

const prevTaskPage = () => {
  if (taskPage.value > 0) {
    taskPage.value--
    fetchData()
  }
}

// Create Project Handler
const createProject = async () => {
  try {
    errorMessage.value = ''
    const token = getToken()
    await $fetch('http://localhost:8080/api/projects', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: newProject.value
    })
    newProject.value = { name: '', description: '' }
    await fetchData()
  } catch (error) {
    console.error('Create project error:', error)
    if (error.data && error.data.message) {
      errorMessage.value = `Validation Error: ${error.data.message}`
    } else {
      errorMessage.value = 'Failed to create project.'
    }
  }
}

// Create Task Handler
const createTask = async () => {
  try {
    errorMessage.value = ''
    const token = getToken()
    await $fetch('http://localhost:8080/api/tasks', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: newTask.value
    })
    newTask.value = { title: '', description: '', projectId: '' }
    await fetchData()
  } catch (error) {
    console.error('Create task error:', error)
    errorMessage.value = 'Failed to create task.'
  }
}

// Delete Project Handler
const deleteProject = async (id) => {
  if (!confirm('Are you sure you want to delete this project?')) return
  try {
    const token = getToken()
    await $fetch(`http://localhost:8080/api/projects/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    await fetchData()
  } catch (error) {
    console.error('Delete project error:', error)
    errorMessage.value = 'Failed to delete project.'
  }
}

// Delete Task Handler
const deleteTask = async (id) => {
  if (!confirm('Are you sure you want to delete this task?')) return
  try {
    const token = getToken()
    await $fetch(`http://localhost:8080/api/tasks/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    })
    await fetchData()
  } catch (error) {
    console.error('Delete task error:', error)
    errorMessage.value = 'Failed to delete task.'
  }
}

const logout = () => {
  localStorage.removeItem('keycloak_token')
  router.push('/login')
}
</script>