<template>
  <div class="page-container">
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon class="header-icon"><TrendCharts /></el-icon> 数据趋势分析
          </span>
          <el-tag effect="dark" type="primary" round>实时</el-tag>
        </div>
      </template>
      <div id="mainChart" style="width: 100%; height: 350px;"></div>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <div class="toolbar">
        <div class="left-actions">
          <el-button type="success" @click="handleAdd" icon="Upload" plain>模拟上报数据</el-button>
          <el-button type="primary" @click="loadData" icon="RefreshRight" circle title="刷新数据"></el-button>
        </div>
        <div class="right-info">
          <span class="info-text">共监测到 <b>{{ total }}</b> 条数据记录</span>
        </div>
      </div>

      <el-table
          :data="tableData"
          border
          stripe
          style="width: 100%"
          height="450"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
      >
        <el-table-column prop="dataId" label="#" width="70" align="center" fixed />

        <el-table-column prop="sensorId" label="传感器ID" width="100" align="center">
          <template #default="scope">
            <el-tag type="info" effect="plain" size="small">ID: {{ scope.row.sensorId }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="value" label="采集数值" min-width="140" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.value > scope.row.alarmThreshold ? 'danger' : 'success'"
                effect="dark"
                class="value-tag"
            >
              <span class="value-text">{{ scope.row.value }}</span>
              <el-icon v-if="scope.row.value > scope.row.alarmThreshold" class="alarm-icon"><Warning /></el-icon>
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="alarmThreshold" label="报警阈值" width="120" align="center">
          <template #default="scope">
            <span class="threshold-text">≤ {{ scope.row.alarmThreshold }}</span>
          </template>
        </el-table-column>

        <el-table-column label="采集时间" width="220" align="center">
          <template #default="scope">
            <div class="time-wrapper">
              <el-icon><Calendar /></el-icon>
              <span class="date-text">{{ scope.row.dataDate }}</span>
              <span class="time-split">|</span>
              <span class="time-text">{{ scope.row.dataTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑修正" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除数据" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row.dataId)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" @current-change="handlePageChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.dataId ? '修正数据记录' : '模拟设备上报'" width="400px" append-to-body>
      <el-form :model="form" label-width="90px" class="custom-form">
        <el-form-item label="传感器ID">
          <el-input v-model="form.sensorId" placeholder="输入ID" />
        </el-form-item>
        <el-form-item label="采集数值">
          <el-input v-model="form.value" type="number" placeholder="例如: 25.6">
            <template #append>单位</template>
          </el-input>
        </el-form-item>
        <el-form-item label="报警阈值">
          <el-input v-model="form.alarmThreshold" type="number" />
        </el-form-item>
        <el-form-item label="采集日期">
          <el-date-picker v-model="form.dataDate" type="date" value-format="YYYY-MM-DD" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="采集时间">
          <el-time-picker v-model="form.dataTime" value-format="HH:mm:ss" style="width: 100%"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">提交数据</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
// 引入更多图标
import { TrendCharts, Upload, RefreshRight, Warning, Calendar, Edit, Delete } from '@element-plus/icons-vue'

const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const form = ref({})
let myChart = null

const initChart = () => {
  const chartDom = document.getElementById('mainChart')
  myChart = echarts.init(chartDom)
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value' },
    series: [{ data: [], type: 'line', smooth: true }]
  }
  myChart.setOption(option)
}

const updateChart = (dataList) => {
  if (!myChart) return
  // 保持你刚才修复的排序逻辑
  const sortedData = [...dataList].sort((a, b) => {
    const timeA = new Date(`${a.dataDate} ${a.dataTime}`).getTime()
    const timeB = new Date(`${b.dataDate} ${b.dataTime}`).getTime()
    return timeA - timeB
  })
  const xData = sortedData.map(item => {
    const shortDate = item.dataDate ? item.dataDate.substring(5) : ''
    return `${shortDate}\n${item.dataTime}`
  })
  const yData = sortedData.map(item => {
    const isAlarm = item.value > item.alarmThreshold;
    return {
      value: item.value,
      itemStyle: {
        color: isAlarm ? '#F56C6C' : '#409EFF',
        borderColor: isAlarm ? '#F56C6C' : '#409EFF'
      },
      symbolSize: isAlarm ? 10 : 6,
      label: { show: isAlarm, position: 'top', color: '#F56C6C', fontWeight: 'bold' }
    }
  })
  myChart.setOption({
    xAxis: { data: xData, axisLabel: { interval: 0, rotate: 0 } },
    series: [{ data: yData }]
  })
}

const loadData = () => {
  axios.get(`http://localhost:8080/data/page`, {
    params: { pageNum: pageNum.value, pageSize: pageSize.value }
  }).then(res => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
      nextTick(() => {
        if (!myChart) initChart()
        updateChart(tableData.value)
      })
    }
  })
}

const handlePageChange = (val) => { pageNum.value = val; loadData() }
const handleAdd = () => { form.value = {}; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }

const save = () => {
  const req = form.value.dataId ? axios.put('http://localhost:8080/data/edit', form.value) : axios.post('http://localhost:8080/data/add', form.value)
  req.then(res => {
    if (res.data.code === 200) { ElMessage.success('成功'); dialogVisible.value = false; loadData() }
    else { ElMessage.error(res.data.msg) }
  }).catch(() => ElMessage.error('失败'))
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除?', '提示', { type: 'warning' }).then(() => {
    axios.delete(`http://localhost:8080/data/delete/${id}`).then(res => {
      if (res.data.code === 200) { ElMessage.success('已删除'); loadData() }
    })
  })
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', () => myChart && myChart.resize())
})
</script>

<style scoped>
.page-container { padding: 20px; }
.chart-card { margin-bottom: 20px; border-radius: 8px; border: none; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05); }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-title { font-size: 16px; font-weight: bold; color: #303133; display: flex; align-items: center; gap: 6px; }
.header-icon { color: #409EFF; font-size: 18px; }

.table-card { border-radius: 8px; border: none; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.info-text { font-size: 13px; color: #909399; }

/* 数值标签样式 */
.value-tag { min-width: 80px; font-family: 'Consolas', monospace; font-size: 14px; letter-spacing: 1px; justify-content: space-between; }
.value-text { font-weight: bold; }
.alarm-icon { margin-left: 5px; font-size: 14px; }

.threshold-text { color: #909399; font-family: monospace; font-size: 12px; }

/* 时间列样式 */
.time-wrapper { display: flex; align-items: center; justify-content: center; color: #606266; font-size: 13px; }
.date-text { margin-left: 6px; }
.time-split { margin: 0 8px; color: #DCDFE6; }
.time-text { font-family: monospace; color: #303133; font-weight: bold; }

.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.custom-form { padding: 0 20px; }
</style>