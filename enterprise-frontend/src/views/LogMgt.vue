<template>
  <div class="page-container">

    <div class="action-header">
      <div class="header-left">
        <div class="page-title">
          <el-icon><Notebook /></el-icon> 运维时光轴
        </div>
        <span class="subtitle">记录每一次系统维护与故障处理</span>
      </div>
      <div class="header-right">
        <el-input
            v-model="searchHandler"
            placeholder="🔍 搜索处理人..."
            class="search-input"
            clearable
            @clear="loadData"
            @keyup.enter="loadData"
        />
        <el-button type="primary" round icon="Plus" @click="handleAdd" class="add-btn">
          新增记录
        </el-button>
      </div>
    </div>

    <div class="timeline-wrapper" v-loading="loading">
      <el-timeline v-if="tableData.length > 0">

        <el-timeline-item
            v-for="(item, index) in tableData"
            :key="item.logId"
            :timestamp="item.logTime"
            placement="top"
            :color="index === 0 ? '#409EFF' : '#E4E7ED'"
            :hollow="index === 0"
        >
          <el-card class="log-card">
            <div class="card-content">

              <div class="user-avatar" :style="{ background: getRandomColor(item.handler) }">
                {{ item.handler ? item.handler.charAt(0) : 'U' }}
              </div>

              <div class="log-info">
                <div class="log-header">
                  <span class="handler-name">{{ item.handler }}</span>
                  <span class="action-text">执行了维护操作</span>
                  <el-tag size="small" effect="plain" round class="location-tag">
                    <el-icon><Location /></el-icon> {{ item.location }}
                  </el-tag>
                </div>
                <div class="log-desc">
                  {{ item.eventDesc }}
                </div>
              </div>

              <div class="card-actions">
                <el-button circle size="small" icon="Edit" @click="handleEdit(item)"></el-button>
                <el-button circle size="small" type="danger" icon="Delete" @click="handleDelete(item.logId)"></el-button>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-else description="暂无维护记录，系统运行良好" :image-size="200" />

      <div class="pagination-box" v-if="total > 0">
        <el-pagination small layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="handlePageChange" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.logId ? '编辑记录' : '记录新事件'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="custom-form">
        <el-form-item label="处理人员">
          <el-input v-model="form.handler" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="维护时间">
          <el-date-picker v-model="form.logTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="维护地点">
          <el-input v-model="form.location" placeholder="例如：3号机房" />
        </el-form-item>
        <el-form-item label="事件描述">
          <el-input v-model="form.eventDesc" type="textarea" :rows="4" placeholder="详细描述起因、经过和结果..." resize="none" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">提交记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Notebook, Location, Edit, Delete, Plus } from '@element-plus/icons-vue'

const tableData = ref([])
const searchHandler = ref('')
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})
const loading = ref(false)

// 随机颜色生成器，让头像多彩一点
const getRandomColor = (name) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#b37feb']
  if(!name) return colors[0]
  return colors[name.length % colors.length]
}

const loadData = () => {
  loading.value = true
  axios.get(`http://localhost:8080/log/page`, {
    params: { pageNum: pageNum.value, pageSize: pageSize.value, handler: searchHandler.value }
  }).then(res => {
    loading.value = false
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  })
}

const handlePageChange = (val) => { pageNum.value = val; loadData() }
const handleAdd = () => { form.value = {}; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }

const save = () => {
  const req = form.value.logId ? axios.put('http://localhost:8080/log/edit', form.value) : axios.post('http://localhost:8080/log/add', form.value)
  req.then(res => {
    if (res.data.code === 200) { ElMessage.success('记录已保存'); dialogVisible.value = false; loadData() }
    else { ElMessage.error(res.data.msg) }
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除这条记录吗?', '提示', { type: 'warning' }).then(() => {
    axios.delete(`http://localhost:8080/log/delete/${id}`).then(res => {
      if (res.data.code === 200) { ElMessage.success('删除成功'); loadData() }
    })
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container {
  padding: 20px 40px; /* 左右留宽一点，更有呼吸感 */
  max-width: 1200px;
  margin: 0 auto;
}

/* 1. 顶部 Header */
.action-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}
.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}
.subtitle {
  font-size: 13px;
  color: #909399;
  margin-top: 5px;
  display: block;
  margin-left: 34px;
}
.header-right {
  display: flex;
  gap: 15px;
}
.search-input { width: 220px; }
.add-btn {
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

/* 2. 时间轴卡片 */
.timeline-wrapper {
  padding: 0 20px;
}
/* 覆盖 Element 默认样式，让时间轴更粗 */
:deep(.el-timeline-item__node) {
  width: 14px; height: 14px; left: -2px;
}
:deep(.el-timeline-item__tail) {
  border-left: 2px solid #E4E7ED;
}
:deep(.el-timeline-item__timestamp) {
  font-size: 14px;
  font-weight: bold;
  color: #909399;
  margin-bottom: 12px;
}

.log-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: all 0.3s ease;
  border-radius: 12px;
  background: #fff;
  position: relative;
  overflow: visible; /* 允许阴影溢出 */
}

.log-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.08);
}

.card-content {
  display: flex;
  align-items: flex-start;
  padding: 5px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  margin-right: 16px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.log-info {
  flex: 1;
}

.log-header {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.handler-name {
  font-weight: bold;
  color: #303133;
  font-size: 16px;
}

.action-text {
  color: #909399;
  font-size: 13px;
}

.location-tag {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
  color: #67c23a;
}

.log-desc {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
  background: #f9fafc;
  padding: 10px;
  border-radius: 6px;
  border-left: 3px solid #dcdfe6;
}

/* 按钮悬浮显示 */
.card-actions {
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  gap: 5px;
  align-self: center;
}
.log-card:hover .card-actions {
  opacity: 1;
}

.pagination-box {
  text-align: center;
  margin-top: 30px;
}
</style>