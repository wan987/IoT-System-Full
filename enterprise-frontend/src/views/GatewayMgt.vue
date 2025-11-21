<template>
  <div class="page-container">
    <div class="glass-panel">

      <div class="toolbar">
        <div class="left-tools">
          <el-input
              v-model="searchName"
              placeholder="🔍 搜索网关名称/IP..."
              class="tech-input dark-input"
              @keyup.enter="loadData"
          />
          <el-button type="primary" color="#409EFF" @click="loadData" class="glow-btn">EXECUTE</el-button>
        </div>
        <el-button type="success" plain @click="handleAdd" icon="Plus" class="add-btn">ADD GATEWAY</el-button>
      </div>

      <el-table
          :data="tableData"
          style="width: 100%"
          class="transparent-table"
          highlight-current-row
      >
        <el-table-column prop="gatewayId" label="#" width="60" align="center" />

        <el-table-column prop="gatewayName" label="DEVICE NAME" min-width="140">
          <template #default="scope">
            <span style="font-weight: bold; color: #fff;">{{ scope.row.gatewayName }}</span>
          </template>
        </el-table-column>

        <el-table-column label="NETWORK (IP:PORT)" min-width="200">
          <template #default="scope">
            <div class="mono-tag">
              {{ scope.row.gatewayIp }}:{{ scope.row.gatewayPort }}
              <el-icon class="copy-icon"><CopyDocument /></el-icon>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="gatewayLoc" label="LOCATION" />

        <el-table-column label="STATUS" width="120" align="center">
          <template #default="scope">
            <div class="status-indicator">
              <span class="dot" :class="scope.row.status === '0' ? 'online' : 'offline'"></span>
              <span class="status-text" :style="{color: scope.row.status === '0' ? '#67C23A' : '#F56C6C'}">
                {{ scope.row.status === '0' ? 'ONLINE' : 'OFFLINE' }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="ACTION" width="160" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">EDIT</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row.gatewayId)">DROP</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="handlePageChange" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.gatewayId ? 'Edit Gateway' : 'New Gateway'" width="40%">
      <el-form :model="form" label-width="100px">
        <el-form-item label="所属站点">
          <el-select v-model="form.stationId" placeholder="选择站点" style="width: 100%">
            <el-option v-for="item in stations" :key="item.stationId" :label="item.stationName" :value="item.stationId" />
          </el-select>
        </el-form-item>
        <el-form-item label="网关名称"><el-input v-model="form.gatewayName" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="form.gatewayLoc" /></el-form-item>
        <el-form-item label="IP地址"><el-input v-model="form.gatewayIp" /></el-form-item>
        <el-form-item label="端口"><el-input v-model="form.gatewayPort" /></el-form-item>
        <el-form-item label="DTU"><el-input v-model="form.gatewayDtu" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, CopyDocument } from '@element-plus/icons-vue'

const tableData = ref([]); const stations = ref([]); const searchName = ref(''); const total = ref(0); const pageNum = ref(1); const pageSize = ref(10); const dialogVisible = ref(false); const form = ref({})
const loadData = () => { axios.get(`http://localhost:8080/gateway/page`, { params: { pageNum: pageNum.value, pageSize: pageSize.value, name: searchName.value } }).then(res => { if (res.data.code === 200) { tableData.value = res.data.data.records; total.value = res.data.data.total } }) }
const loadStations = () => { axios.get('http://localhost:8080/station/list').then(res => { if (res.data.code === 200) { stations.value = res.data.data } }) }
const handlePageChange = (val) => { pageNum.value = val; loadData() }
const handleAdd = () => { form.value = { status: '0' }; dialogVisible.value = true; loadStations() }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true; loadStations() }
const save = () => { if (!form.value.gatewayName) return ElMessage.warning('请填写名称'); const req = form.value.gatewayId ? axios.put('http://localhost:8080/gateway/edit', form.value) : axios.post('http://localhost:8080/gateway/add', form.value); req.then(res => { if (res.data.code === 200) { ElMessage.success('Success'); dialogVisible.value = false; loadData() } }) }
const handleDelete = (id) => { ElMessageBox.confirm('Delete?', 'Warning', { type: 'warning' }).then(() => { axios.delete(`http://localhost:8080/gateway/delete/${id}`).then(res => { if (res.data.code === 200) { ElMessage.success('Deleted'); loadData() } }) }) }
onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 30px; }

/* --- 核心：深色磨砂面板 --- */
.glass-panel {
  background: rgba(15, 23, 42, 0.6); /* 深色背景 */
  backdrop-filter: blur(20px);       /* 磨砂效果 */
  border: 1px solid rgba(255, 255, 255, 0.1); /* 微光边框 */
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  color: #fff;
}

.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; }
.left-tools { display: flex; gap: 10px; }

/* 输入框深色适配 */
.tech-input { width: 240px; font-family: monospace; }
:deep(.dark-input .el-input__wrapper) {
  background-color: rgba(0,0,0,0.3);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.2) inset;
  color: white;
}
:deep(.dark-input .el-input__inner) { color: white; }

.glow-btn { box-shadow: 0 0 10px rgba(64,158,255,0.4); }

/* --- 表格透明化黑魔法 --- */
.transparent-table {
  background-color: transparent !important;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.05) !important;
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.1) !important;
  --el-table-border-color: rgba(255, 255, 255, 0.05);
  --el-table-text-color: #ddd;
  --el-table-header-text-color: #fff;
  color: #ddd;
}
/* 强制去白底 */
:deep(.el-table__inner-wrapper), :deep(.el-table th), :deep(.el-table tr) {
  background-color: transparent !important;
}
/* 去掉底部白线 */
:deep(.el-table__inner-wrapper::before) { display: none; }

/* IP 标签样式 */
.mono-tag {
  font-family: 'Consolas', monospace; font-size: 12px;
  background-color: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(64, 158, 255, 0.3);
  color: #409EFF;
  padding: 4px 8px; border-radius: 4px;
  display: inline-flex; align-items: center;
}
.copy-icon { margin-left: 8px; cursor: pointer; opacity: 0.7; }
.copy-icon:hover { opacity: 1; color: #fff; }

/* 状态灯 */
.status-indicator { display: flex; align-items: center; justify-content: center; gap: 8px; }
.dot { width: 6px; height: 6px; border-radius: 50%; display: inline-block; }
.online { background-color: #67C23A; box-shadow: 0 0 8px #67C23A; }
.offline { background-color: #F56C6C; box-shadow: 0 0 8px #F56C6C; }
.status-text { font-size: 12px; font-weight: bold; font-family: monospace; }

.pagination-bar { margin-top: 20px; display: flex; justify-content: flex-end; }
/* 分页器文字颜色修正 */
:deep(.el-pagination__total), :deep(.el-pagination__jump) { color: #ccc; }
</style>