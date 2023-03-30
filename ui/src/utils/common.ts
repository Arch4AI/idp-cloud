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
