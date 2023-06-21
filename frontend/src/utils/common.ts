/**
 * 检查手机号码是不是正确的
 * @param phone
 * @returns
 */
export const checkPhone = (phone: string): boolean => {
  const regexp = new RegExp(/^(?:(?:\+|00)86)?1\d{10}$/) //123是正则表达式内容
  if (!phone || !regexp.test(phone)) {
    return false
  }
  return true
}

/**
 * 校验邮箱地址
 * @param email
 * @returns
 */
export const checkEmail = (email: string): boolean => {
  const regexp = new RegExp(
    /^[A-Za-z0-9\u4e00-\u9fa5_]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  ) //123是正则表达式内容
  if (!email || !regexp.test(email)) {
    return false
  }
  return true
}

/**
 * 校验密码强度
 * @param password
 * @returns
 */
export const checkPassword = (password: string): boolean => {
  const regexp = new RegExp(
    /^\S*(?=\S{6,})(?=\S*\d)(?=\S*[A-Z])(?=\S*[a-z])(?=\S*[!@#$%^&*.?])\S*$/
  ) //123是正则表达式内容
  if (!password || !regexp.test(password)) {
    return false
  }
  return true
}

/**
 * 校验用户名规则
 * @param userName
 * @returns
 */
export const checkUserName = (userName: string) => {
  const regexp = new RegExp(/^[\w-]{4,16}$/) //123是正则表达式内容
  if (!userName || !regexp.test(userName)) {
    return false
  }
  return true
}
