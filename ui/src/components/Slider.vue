<template>
  <div
    class="slide-verify"
    :style="{ width: canvasWidth + 'px' }"
    onselectstart="return false;"
  >
    <!-- 图片加载遮蔽罩 -->
    <div
      class="img-loading"
      :style="{ height: canvasHeight + 'px' }"
      v-if="isLoading"
    />
    <!-- 认证成功后的文字提示 -->
    <div
      class="success-hint"
      :style="{ height: canvasHeight + 'px' }"
      v-if="verifySuccess"
    >
      {{ successHint }}
    </div>
    <!--刷新按钮-->
    <img
      class="refresh-icon"
      src="../assets/svg/refresh.svg"
      @click="refresh"
    />
    <!--验证图片-->
    <img
      ref="canvas"
      class="slide-canvas"
      :width="canvasWidth"
      :height="canvasHeight"
    />
    <!--阻塞块-->
    <img ref="block" :class="['slide-block', { 'verify-fail': verifyFail }]" />
    <!-- <div class="tips success" v-if="isPassing">{{successTip}}</div> -->
    <div class="tips danger" v-if="isPassing">{{ failTip }}</div>
    <!-- 滑动条 -->
    <div
      class="slider"
      :class="{
        'verify-active': verifyActive,
        'verify-success': verifySuccess,
        'verify-fail': verifyFail
      }"
    >
      <!--滑动条提示文字-->
      <div
        class="slider-hint"
        style="width: 335px; height: 35px; font-size: 14px"
      >
        {{ sliderHint }}
      </div>
      <!--滑块-->
      <div class="slider-box" :style="{ width: sliderBoxWidth }">
        <!-- 按钮 -->
        <div
          class="slider-button"
          id="slider-button"
          :style="{ left: sliderButtonLeft }"
        >
          <!-- 按钮图标 -->
          <div class="el-icon-d-arrow-right" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, getCurrentInstance, ref, Ref, defineEmits } from 'vue'
import { getCaptcha } from '@/api/common/common'
const { proxy }: any = getCurrentInstance()
// 抛出可以执行的方法
// 暂时需要在后端去验证是否通过了 这个地方需要直接将横坐标传递给后端 再去校验 校验成功后去触发对应的信息
// 暂时支持bindEvent 但是在卸载组件的时候并未移除Event 需要优化
const emit = defineEmits(['success', 'fail'])
const props = defineProps({
  // 阻塞块长度
  blockLength: {
    type: Number,
    default: () => 42
  },
  // 阻塞块弧度
  blockRadius: {
    type: Number,
    default: () => 10
  },
  // 画布宽度
  canvasWidth: {
    type: Number,
    default: () => 355
  },
  // 画布高度
  canvasHeight: {
    type: Number,
    default: () => 188
  },
  // 滑块操作提示
  sliderHint: {
    type: String,
    default: () => '请按住滑块拖动'
  },
  // 可允许的误差范围小；为1时，则表示滑块要与凹槽完全重叠，才能验证成功。默认值为5，若为 -1 则不进行机器判断
  accuracy: {
    type: Number,
    default: () => 3
  },
  // 图片资源数组
  imageList: {
    type: Array,
    default: () => []
  },
  failTip: {
    type: String,
    default: () => '验证未通过，拖动滑块将悬浮图像正确合并'
  }
})

// 校验进行状态
const verifyActive = ref(false)
// 校验成功状态
const verifySuccess = ref(false)
// 校验失败状态
const verifyFail = ref(false)
// 阻塞块对象
let blockObj: any = null
// 图片画布对象
let canvasCtx: any = null
// 阻塞块画布对象
let blockCtx: any = null
// 阻塞块宽度
const blockWidth = props.blockLength * 2
// 阻塞块的横轴坐标
let blockX: any = undefined
// 阻塞块的纵轴坐标
let blockY: any = undefined
// 图片对象
let image: any = undefined
// 移动的X轴坐标
let originX: any = undefined
// 移动的Y轴做坐标
let originY: any = undefined
// 拖动距离数组
const dragDistanceList: any[] = []
// 滑块箱拖动宽度
const sliderBoxWidth: any = ref(0)
// 滑块按钮距离左侧起点位置
const sliderButtonLeft: any = ref(0)
// 鼠标按下状态
let isMouseDown: boolean = false
// 图片加载提示，防止图片没加载完就开始验证
const isLoading = ref(true)
// 时间戳，计算滑动时长
let timestamp: any = null
// 成功提示
const successHint = ref('')
// 随机字符串
let ticket: string = ''
// 是否显示状态文字
let isPassing = ref(false)
const block: any = ref()
const canvas: any = ref()
onMounted(() => {
  init()
})
/* 初始化 */
const init = () => {
  initDom()
  bindEvents()
}
/* 初始化DOM对象 */
const initDom = () => {
  blockObj = block.value
  getCaptchaApi()
}
/* 后台获取验证码 */
const getCaptchaApi = async () => {
  // 取后端默认值
  const data = {
    canvasWidth: props.canvasWidth,
    canvasHeight: props.canvasHeight,
    blockWidth: blockWidth / 1.5,
    blockHeight: blockWidth / 1.5,
    blockRadius: props.blockRadius,
    place: 1
  }
  // 获取后台滑块验证码
  await getCaptcha(data)
    .then((response: any) => {
      const data = response.data
      ticket = data.ticket
      block.value.src = data.blockSrc
      block.value.style.top = data.blockY + 'px'
      canvas.value.src = data.canvasSrc
    })
    .finally(() => {
      isLoading.value = false
    })
}
/* 事件绑定 */
const bindEvents = () => {
  // 监听鼠标按下事件
  document
    .getElementById('slider-button')
    ?.addEventListener('mousedown', (event) => {
      startEvent(event.clientX, event.clientY)
    })
  // 监听鼠标移动事件
  document.addEventListener('mousemove', (event) => {
    moveEvent(event.clientX, event.clientY)
  })
  // 监听鼠标离开事件
  document.addEventListener('mouseup', (event) => {
    endEvent(event.clientX)
  })
  // 监听触摸开始事件
  document
    .getElementById('slider-button')
    ?.addEventListener('touchstart', (event) => {
      startEvent(event.changedTouches[0].pageX, event.changedTouches[0].pageY)
    })
  // 监听触摸滑动事件
  document.addEventListener('touchmove', (event) => {
    moveEvent(event.changedTouches[0].pageX, event.changedTouches[0].pageY)
  })
  // 监听触摸离开事件
  document.addEventListener('touchend', (event) => {
    endEvent(event.changedTouches[0].pageX)
  })
}
/* 校验图片是否存在 */
const checkImgSrc = () => {
  return !!canvas.value.src
}
/* 滑动开始事件 */
const startEvent = (originXx: number, originYy: number) => {
  isPassing.value = false
  if (!checkImgSrc() || isLoading.value || verifySuccess.value) {
    return
  }
  originX = originXx
  originY = originYy
  isMouseDown = true
  timestamp = +new Date()
}
/* 滑动事件 */
const moveEvent = (originXx: number, originYy: number) => {
  if (!isMouseDown) {
    return false
  }
  const moveX = originXx - originX
  const moveY = originYy - originY
  if (moveX < 0 || moveX + 40 >= props.canvasWidth) {
    return false
  }
  sliderButtonLeft.value = moveX + 'px'
  let blockLeft =
    ((props.canvasWidth - 40 - 20) / (props.canvasWidth - 40)) * moveX
  blockObj.style.left = blockLeft + 'px'
  verifyActive.value = true
  sliderBoxWidth.value = moveX + 'px'
  dragDistanceList.push(moveY)
}
/* 滑动结束事件 */
const endEvent = (originXx: number) => {
  if (!isMouseDown) {
    return false
  }
  isMouseDown = false
  if (originXx === originX) {
    return false
  }

  // 开始校验
  isLoading.value = true
  // 校验结束
  verifyActive.value = false
  // 滑动时长
  timestamp = +new Date() - timestamp
  // 移动距离
  const moveLength = parseInt(blockObj.style.left)
  // 限制操作时长10S，超出判断失败
  if (timestamp > 10000) {
    verifyFailEvent()
  }
  emit('success', { ticket: ticket, moveLength: moveLength })
}
/* 校验成功 */
const verifySuccessEvent = () => {
  isLoading.value = false
  verifySuccess.value = true
  const elapsedTime = Number((timestamp / 1000).toFixed(1))
  if (elapsedTime < 1) {
    successHint.value = `仅仅${elapsedTime}S,你的速度快如闪电`
  } else if (elapsedTime < 2) {
    successHint.value = `只用了${elapsedTime}S,这速度简直完美`
  } else {
    successHint.value = `耗时${elapsedTime}S,争取下次再快一点`
  }
}
/* 校验失败 */
const verifyFailEvent = (msg?: string) => {
  verifyFail.value = true
  emit('fail', msg)
  refresh()
}
/* 刷新图片验证码 */
const refresh = () => {
  // 延迟class的删除，等待动画结束
  setTimeout(() => {
    verifyFail.value = false
  }, 500)
  isLoading.value = true
  verifyActive.value = false
  verifySuccess.value = false
  blockObj.style.left = 0
  sliderBoxWidth.value = 0
  sliderButtonLeft.value = 0
  getCaptchaApi()
}
defineExpose({
  verifyFailEvent,
  verifySuccessEvent,
  isPassing,
  refresh
})
</script>

<style scoped lang="scss">
::v-deep.slider-box #slider-button .el-icon-d-arrow-right {
  color: #e8e8e8;
}
</style>
<style scoped lang="scss">
.slide-verify {
  position: relative;
}

/* 图片加载样式 */
.img-loading {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 999;
  border-radius: 5px;
  background-position: center center;
  background-repeat: no-repeat;
  background-size: 100px;
  background-color: #737c8e;
  animation: loading 1.5s infinite;
  background-image: url(../assets/svg/loading.svg);
}

@keyframes loading {
  0% {
    opacity: 0.7;
  }

  100% {
    opacity: 9;
  }
}

/* 认证成功后的文字提示 */
.success-hint {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: large;
  color: #2cd000;
  background: rgb(255 255 255 / 80%);
}

/* 刷新按钮 */
.refresh-icon {
  position: absolute;
  top: 0;
  right: 0;
  width: 35px;
  height: 35px;
  // background: url("../assets/svg/refresh.svg");
  // background-size: 35px 470px;
  cursor: pointer;
}

/* 阻塞块 */
.slide-block {
  position: absolute;
  top: 0;
  left: 0;
}

/* 校验失败时的阻塞块样式 */
.slide-block.verify-fail {
  transition: left 0.5s linear;
}

/* 滑动条 */
.slider {
  position: relative;
  margin-top: 10px;
  border: 1px solid #e4e7eb;
  border-radius: 5px;
  width: 100%;
  height: 35px;
  text-align: center;
  color: #45494c;
  // background-color: #e8e8e8;
  line-height: 40px;
}

/* 滑动盒子 */
.slider-box {
  position: absolute;
  top: 0;
  left: 0;
  border: 0 solid #1991fa;
  border-radius: 5px;
  height: 35px;
  background: #d1e9fe;
}

/* 滑动按钮 */
.slider-button {
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 5px;
  width: 40px;
  height: 35px;
  background: #fff;
  box-shadow: 0 0 3px rgb(0 0 0 / 30%);
  transition: background 0.2s linear;
  cursor: move;
}

/* 鼠标悬浮时的按钮样式 */
.slider-button:hover {
  background: #3065ea;
}

.slider-button {
  background: #fafafa;
}

/* 鼠标悬浮时的按钮图标样式 */
.slider-button:hover .slider-button-icon {
  background-position: 0 -13px;
}

/* 滑动按钮图标 */
.slider-button-icon {
  position: absolute;
  top: 15px;
  left: 13px;
  width: 15px;
  height: 13px;
  background: url('../../../assets/images/light.png') 0 -26px;
  background-size: 35px 470px;
}

/* 校验时的按钮样式 */
.verify-active .slider-button {
  top: -1px;
  border: 1px solid #1991fa;
  height: 33px;
}

/* 校验时的滑动箱样式 */
.verify-active .slider-box {
  border-width: 1px;
  height: 33px;
}

/* 校验成功时的滑动箱样式 */
.verify-success .slider-box {
  border: 1px solid #52ccba;
  height: 33px;
  background-color: #d2f4ef;
}

/* 校验成功时的按钮样式 */
.verify-success .slider-button {
  top: -1px;
  border: 1px solid #52ccba;
  height: 33px;
  background-color: #52ccba !important;
}

/* 校验成功时的按钮图标样式 */
.verify-success .slider-button-icon {
  background-position: 0 0 !important;
}

/* 校验失败时的滑动箱样式 */
.verify-fail .slider-box {
  border: 1px solid #f57a7a;
  height: 33px;
  background-color: #fce1e1;
  transition: width 0.5s linear;
}

/* 校验失败时的按钮样式 */
.verify-fail .slider-button {
  top: -1px;
  border: 1px solid #f57a7a;
  height: 33px;
  background-color: #f57a7a !important;
  transition: left 0.5s linear;
}

/* 校验失败时的按钮图标样式 */
.verify-fail .slider-button-icon {
  top: 14px;
  background-position: 0 -82px !important;
}

@keyframes slidetounlock {
  0% {
    background-position: -175px 0;
  }

  100% {
    background-position: 175px 0;
  }
}

.slider .slider-hint {
  position: absolute;
  top: 0;
  width: 100%;
  height: 0.61rem;
  font-size: 0.16rem;
  //color: transparent;
  background: -webkit-gradient(
    linear,
    left top,
    right top,
    color-stop(0, #fff),
    color-stop(0.4, #fff),
    color-stop(0.5, #fff),
    color-stop(0.6, #fff),
    color-stop(1, #fff)
  );
  background-clip: text;
  // user-select: none;
  // -webkit-text-fill-color: transparent;
  text-size-adjust: none;
  animation: slidetounlock 3s infinite;
}

.tips {
  position: absolute;
  bottom: 57px;
  z-index: 200;
  width: 100%;
  height: 20px;
  font-size: 12px;
  text-align: center;
  line-height: 20px;
}

.tips.success {
  color: green;
  background: rgb(255 255 255 / 60%);
}

.tips.danger {
  color: yellow;
  background: rgb(0 0 0 / 60%);
}

/* 校验状态下的提示文字隐藏 */
.verify-active .slider-hint,
.verify-success .slider-hint,
.verify-fail .slider-hint {
  display: none;
}
</style>
