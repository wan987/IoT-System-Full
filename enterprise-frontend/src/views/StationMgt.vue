<template>
  <div class="page-container">
    <el-card class="box-card" shadow="hover">
      <div class="search-bar">
        <div class="search-left">
          <el-input
              v-model="searchName"
              placeholder="请输入站点名称..."
              class="search-input"
              clearable
              @clear="loadData"
              @keyup.enter="loadData"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="loadData" icon="Search">查询</el-button>
        </div>
        <div class="search-right">
          <el-button type="success" @click="handleAdd" icon="Plus">新建站点</el-button>
        </div>
      </div>

      <el-table
          :data="tableData"
          border
          stripe
          style="width: 100%; margin-top: 20px;"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
      >
        <el-table-column prop="stationId" label="ID" width="80" align="center" />

        <el-table-column prop="stationName" label="站点名称" min-width="150">
          <template #default="scope">
            <span style="font-weight: 600; color: #303133;">{{ scope.row.stationName }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="stationLoc" label="位置" min-width="150" />

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.status === '0' ? 'success' : 'danger'"
                effect="light"
                round
            >
              {{ scope.row.status === '0' ? '运行中' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button
                size="small"
                type="primary"
                plain
                icon="Edit"
                @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
                size="small"
                type="danger"
                plain
                icon="Delete"
                @click="handleDelete(scope.row.stationId)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :total="total"
            :page-size="pageSize"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.stationId ? '编辑站点' : '新增站点'" width="30%" destroy-on-close>
      <el-form :model="form" label-width="80px" class="custom-form">
        <el-form-item label="站点名称">
          <el-input v-model="form.stationName" placeholder="例如：长沙一号监测站" />
        </el-form-item>
        <el-form-item label="地理位置">
          <el-input v-model="form.stationLoc" placeholder="例如：岳麓区桃子湖路" />
        </el-form-item>
        <el-form-item label="站点状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0" border>正常运行</el-radio>
            <el-radio label="1" border>停止维护</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入需要的图标
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

const tableData = ref([])
const searchName = ref('')
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})

const loadData = () => {
  // 这里要加上你的权限参数逻辑 (roleType, deptId)，我这里简写了，保留你之前的即可
  const userStr = localStorage.getItem('user')
  let userParams = {}
  if (userStr) {
    const user = JSON.parse(userStr)
    userParams = { roleType: user.roleType, deptId: user.deptId }
  }

  axios.get(`http://localhost:8080/station/page`, {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      name: searchName.value,
      ...userParams
    }
  }).then(res => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    } else {
      ElMessage.error(res.data.msg)
    }
  })
}

const handlePageChange = (val) => { pageNum.value = val; loadData() }
const handleAdd = () => { form.value = { status: '0' }; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }

const save = () => {
  const req = form.value.stationId
      ? axios.put('http://localhost:8080/station/edit', form.value)
      : axios.post('http://localhost:8080/station/add', form.value)
  req.then(res => {
    if (res.data.code === 200) { ElMessage.success('操作成功'); dialogVisible.value = false; loadData() }
    else { ElMessage.error(res.data.msg) }
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该站点吗?', '警告', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
      .then(() => {
        axios.delete(`http://localhost:8080/station/delete/${id}`).then(res => {
          if (res.data.code === 200) { ElMessage.success('删除成功'); loadData() }
        })
      })
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container {
  padding: 20px;
}

/* 卡片样式 */
.box-card {
  border-radius: 8px;
  border: none; /* 去掉边框，靠阴影 */
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

/* 搜索栏布局 */
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5; /* 加一条淡淡的分隔线 */
}
.search-left {
  display: flex;
  gap: 10px;
}
.search-input {
  width: 240px;
}

/* 分页居右 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 弹窗表单微调 */
.custom-form {
  padding: 0 20px;
}
</style>