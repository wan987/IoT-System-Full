<template>
  <div class="page-container">
    <div class="glass-panel">
      <div class="toolbar">
        <div class="left-tools">
          <el-input v-model="searchName" placeholder="🔍 搜索网关名称/IP..." class="tech-input" @keyup.enter="loadData" />
          <el-button type="primary" color="#001529" @click="loadData">EXECUTE</el-button>
        </div>
        <el-button type="success" plain @click="handleAdd" icon="Plus">ADD GATEWAY</el-button>
      </div>

      <el-table
          :data="tableData"
          style="width: 100%"
          :header-cell-style="{ background: '#1f2d3d', color: '#fff', fontSize: '13px', letterSpacing: '1px' }"
          :row-style="{ background: '#f9fafc' }"
          highlight-current-row
      >
        <el-table-column prop="gatewayId" label="#" width="60" align="center" />

        <el-table-column prop="gatewayName" label="DEVICE NAME" min-width="140">
          <template #default="scope">
            <span style="font-weight: bold; color: #2c3e50;">{{ scope.row.gatewayName }}</span>
          </template>
        </el-table-column>

        <el-table-column label="NETWORK (IP:PORT)" min-width="200">
          <template #default="scope">
            <el-tag type="info" effect="dark" class="mono-tag">
              {{ scope.row.gatewayIp }}:{{ scope.row.gatewayPort }}
            </el-tag>
            <el-icon class="copy-icon"><CopyDocument /></el-icon>
          </template>
        </el-table-column>

        <el-table-column prop="gatewayLoc" label="LOCATION" />

        <el-table-column label="STATUS" width="120" align="center">
          <template #default="scope">
            <div class="status-indicator">
              <span class="dot" :class="scope.row.status === '0' ? 'online' : 'offline'"></span>
              <span class="status-text">{{ scope.row.status === '0' ? 'ONLINE' : 'OFFLINE' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="ACTION" width="150" align="center">
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

// ... 逻辑代码保持完全不变，直接复用之前的即可 ...
const tableData = ref([]); const stations = ref([]); const searchName = ref(''); const total = ref(0); const pageNum = ref(1); const pageSize = ref(10); const dialogVisible = ref(false); const form = ref({})
const loadData = () => { axios.get(`http://localhost:8080/gateway/page`, { params: { pageNum: pageNum.value, pageSize: pageSize.value, name: searchName.value } }).then(res => { if (res.data.code === 200) { tableData.value = res.data.data.records; total.value = res.data.data.total } }) }
const loadStations = () => { axios.get('http://localhost:8080/station/list').then(res => { if (res.data.code === 200) { stations.value = res.data.data } }) }
const handlePageChange = (val) => { pageNum.value = val; loadData() }
const handleAdd = () => { form.value = { status: '0' }; dialogVisible.value = true; loadStations() }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true; loadStations() }
const save = () => {
  if (!form.value.gatewayName) return ElMessage.warning('请填写名称')
  const req = form.value.gatewayId ? axios.put('http://localhost:8080/gateway/edit', form.value) : axios.post('http://localhost:8080/gateway/add', form.value)
  req.then(res => { if (res.data.code === 200) { ElMessage.success('Success'); dialogVisible.value = false; loadData() } })
}
const handleDelete = (id) => { ElMessageBox.confirm('Delete?', 'Warning', { type: 'warning' }).then(() => { axios.delete(`http://localhost:8080/gateway/delete/${id}`).then(res => { if (res.data.code === 200) { ElMessage.success('Deleted'); loadData() } }) }) }
onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 20px; }
.glass-panel {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  border-top: 3px solid #001529; /* 顶部加一条深色线，增加稳重感 */
}

.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; }
.left-tools { display: flex; gap: 10px; }
.tech-input { width: 240px; font-family: monospace; }

/* 这里的 mono-tag 是精髓，让数字看起来像代码 */
.mono-tag { font-family: 'Consolas', 'Monaco', monospace; font-size: 12px; letter-spacing: 0.5px; background-color: #eef1f6; color: #48576a; border: 1px solid #d1dbe5; }
.copy-icon { cursor: pointer; margin-left: 8px; color: #909399; font-size: 12px; }
.copy-icon:hover { color: #409EFF; }

/* 呼吸灯效果 */
.status-indicator { display: flex; align-items: center; justify-content: center; gap: 6px; }
.dot { width: 8px; height: 8px; border-radius: 50%; display: inline-block; }
.online { background-color: #67C23A; box-shadow: 0 0 4px #67C23A; animation: pulse 2s infinite; }
.offline { background-color: #F56C6C; }
.status-text { font-size: 12px; font-weight: bold; color: #606266; }

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
.pagination-bar { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>