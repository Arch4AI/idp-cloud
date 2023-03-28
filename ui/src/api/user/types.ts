// 这个是SessionUser 在redis中可以查询到 不返回sessionId
interface LocalUser {
  accountNonExpired: boolean
  accountNonLocked: boolean
  credentialsNonExpired: boolean
  authorities: Authoritity[]
  createTime: Date
  updateTime: Date
  email: string
  enabled: boolean
  gender: number
  language: string
  loginType: string
  nickName: string
  password: string
  phone: string
  userId: string
  userName: string
  username: string
}

// spring 用户绑定角色
interface Authoritity {
  authority: string
}

// 定义一个角色枚举类
enum SysRole {
  ROLE_ADMIN,
  ROLE_USER
}
