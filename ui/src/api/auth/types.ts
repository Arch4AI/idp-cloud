// 外部登录源
interface ExternalAuthSource {
  id: string
  displayName: string
  providerIcon: string
  clientId: string
  clientSecret: string
  clientType: string
  redirectUri: string
  authUri?: string
  tokenUri?: string
  userInfoUri?: string
  logoutUri?: string
  alipayPublicKey?: string
  stackOverflowKey?: string
  agentId?: string
  userType?: string
  domainPrefix?: string
  deviceId?: string
  authServerId?: string
  scope?: string
  openProxy?: boolean
  proxyHost?: string
  proxyPort?: number
  enable: boolean
  createTime?: Date
  updateTime?: Date
}

// 获取到的登录信息
interface ExternalLoginUri {
  id: string
  clientType: string
  uri: string
}
