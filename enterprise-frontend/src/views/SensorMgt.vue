<template>
  <div class="page-container">
    <div class="action-bar">
      <el-input v-model="searchName" placeholder="搜索设备..." class="search-input" @keyup.enter="loadData">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" circle icon="Refresh" @click="loadData" title="刷新"></el-button>
      <el-button type="success" icon="Plus" @click="handleAdd" class="add-btn">部署新设备</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.sensorId">
        <div class="sensor-card">
          <div class="card-header">
            <div class="header-left">
              <div class="status-badge" :class="item.status === '0' ? 'bg-green' : 'bg-red'"></div>
              <span class="sensor-name">{{ item.sensorName }}</span>
            </div>
            <el-dropdown trigger="click">
              <el-icon class="more-btn"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleEdit(item)">编辑配置</el-dropdown-item>
                  <el-dropdown-item divided style="color: #F56C6C" @click="handleDelete(item.sensorId)">拆除设备</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="card-body">
            <div class="icon-wrapper" :class="getTypeClass(item.sensorType)">
              {{ item.sensorType ? item.sensorType.charAt(0) : '感' }}
            </div>
            <div class="info-group">
              <div class="info-label">设备类型</div>
              <div class="info-value">{{ item.sensorType || '通用传感器' }}</div>
            </div>
          </div>

          <div class="card-footer">
            <el-icon><Location /></el-icon>
            <span class="location-text">{{ item.sensorLoc }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="pagination-container" v-if="total > 0">
      <el-pagination layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="handlePageChange" />
    </div>

    <el-empty v-if="tableData.length === 0" description="暂无部署的传感器" />

    <el-dialog v-model="dialogVisible" :title="form.sensorId ? '编辑设备' : '部署新设备'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="所属站点">
          <el-select v-model="form.stationId" placeholder="选择站点" @change="handleStationChange" style="width: 100%">
            <el-option v-for="i in stations" :key="i.stationId" :label="i.stationName" :value="i.stationId" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属网关">
          <el-select v-model="form.gatewayId" placeholder="选择网关" :disabled="!form.stationId" style="width: 100%">
            <el-option v-for="i in gateways" :key="i.gatewayId" :label="i.gatewayName" :value="i.gatewayId" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备名称"><el-input v-model="form.sensorName" /></el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="form.sensorType" style="width: 100%">
            <el-option label="水质传感器" value="水质" />
            <el-option label="气象传感器" value="气象" />
            <el-option label="土壤传感器" value="土壤" />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置"><el-input v-model="form.sensorLoc" /></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio label="0">启用</el-radio><el-radio label="1">停用</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="save">保存配置</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, MoreFilled, Location } from '@element-plus/icons-vue'

// ... 逻辑代码复用之前的 ...
const tableData = ref([]); const stations = ref([]); const gateways = ref([]); const searchName = ref(''); const total = ref(0); const pageNum = ref(1); const pageSize = ref(12); // 卡片模式每页显示12个更好看
const dialogVisible = ref(false); const form = ref({})
const loadData = () => { axios.get(`http://localhost:8080/sensor/page`, { params: { pageNum: pageNum.value, pageSize: pageSize.value, name: searchName.value } }).then(res => { if (res.data.code === 200) { tableData.value = res.data.data.records; total.value = res.data.data.total } }) }
const loadStations = () => { axios.get('http://localhost:8080/station/list').then(res => { if (res.data.code === 200) stations.value = res.data.data }) }
const handleStationChange = (sid) => { form.value.gatewayId = null; gateways.value = []; if(!sid) return; axios.get(`http://localhost:8080/gateway/listByStation?stationId=${sid}`).then(res => { if (res.data.code === 200) gateways.value = res.data.data }) }
const handleAdd = () => { form.value = { status: '0' }; dialogVisible.value = true; loadStations() }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true; loadStations(); if(row.stationId){ handleStationChange(row.stationId); setTimeout(()=>form.value.gatewayId=row.gatewayId, 200) } }
const save = () => { const req = form.value.sensorId ? axios.put('http://localhost:8080/sensor/edit', form.value) : axios.post('http://localhost:8080/sensor/add', form.value); req.then(res => { if (res.data.code === 200) { ElMessage.success('Saved'); dialogVisible.value = false; loadData() } }) }
const handleDelete = (id) => { ElMessageBox.confirm('Remove?', 'Warning').then(() => axios.delete(`http://localhost:8080/sensor/delete/${id}`).then(res => { if (res.data.code === 200) { loadData() } })) }

// 辅助函数：根据类型返回不同的颜色样式
const getTypeClass = (type) => {
  if (type && type.includes('水')) return 'type-water'
  if (type && type.includes('气')) return 'type-air'
  if (type && type.includes('土')) return 'type-earth'
  return 'type-default'
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 20px; }
.action-bar { display: flex; gap: 10px; margin-bottom: 24px; align-items: center; }
.search-input { width: 240px; }
.add-btn { margin-left: auto; } /* 把按钮推到最右边 */

/* --- 核心卡片样式 --- */
.sensor-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.04);
  transition: all 0.3s;
  border: 1px solid #ebeef5;
  position: relative;
  overflow: hidden;
}
.sensor-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  border-color: #b3d8ff;
}

.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.header-left { display: flex; align-items: center; gap: 8px; }
.status-badge { width: 8px; height: 8px; border-radius: 50%; }
.bg-green { background: #67C23A; box-shadow: 0 0 4px #67C23A; }
.bg-red { background: #F56C6C; }
.sensor-name { font-weight: bold; color: #303133; font-size: 15px; }
.more-btn { cursor: pointer; color: #909399; transform: rotate(90deg); }
.more-btn:hover { color: #409EFF; }

.card-body { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; }
.icon-wrapper {
  width: 48px; height: 48px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; font-weight: bold; color: white;
}
/* 不同类型的颜色 */
.type-water { background: linear-gradient(135deg, #36d1dc, #5b86e5); }
.type-air { background: linear-gradient(135deg, #a8c0ff, #3f2b96); }
.type-earth { background: linear-gradient(135deg, #f09819, #edde5d); }
.type-default { background: #909399; }

.info-label { font-size: 12px; color: #909399; margin-bottom: 2px; }
.info-value { font-size: 14px; color: #606266; font-weight: 500; }

.card-footer {
  border-top: 1px solid #f2f6fc;
  padding-top: 12px;
  font-size: 12px; color: #909399;
  display: flex; align-items: center; gap: 4px;
}

.pagination-container { text-align: center; margin-top: 20px; }
</style>