import { defineComponent } from 'vue'

export type Component<T = any> =
  | ReturnType<typeof defineComponent>
  | (() => Promise<typeof import('*.vue')>)
  | (() => Promise<T>)

interface Captcha {
  ticket?: string
  value?: string
  canvasSrc?: string
  canvasWidth?: number
  canvasHeight?: number
  blockSrc?: string
  blockWidth?: number
  blockHeight?: number
  blockRadius?: number
  blockX?: number
  blockY?: number
  place: number
}

export interface MenuRoute {
  path: string
  title?: string
  name?: string
  icon?:
    | string
    | {
        render: () => void
      }
  redirect?: string
  children: MenuRoute[]
  meta: any
}

export interface RouteItem {
  path: string
  name: string
  component?: Component | string
  components?: Component
  redirect?: string
  meta: RouteMeta
  children?: Array<RouteItem>
}
export interface RouteMeta {
  title: string
  icon?: string
  expanded?: boolean
  orderNo?: number
  hidden?: boolean
  hiddenBreadcrumb?: boolean
  single?: boolean
  keepAlive?: boolean
  frameSrc?: string
  frameBlank?: boolean
}
