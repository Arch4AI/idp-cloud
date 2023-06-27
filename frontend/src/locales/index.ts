/* eslint-disable camelcase */
import { createI18n } from 'vue-i18n'
import { DropdownOption } from 'tdesign-vue-next'

import zh_CN from './lang/zh_CN'
import en_US from './lang/en_US'

export type LangName = 'zh_CN' | 'en_US'

const messages = {
  zh_CN,
  en_US
}
const browserLanguage = navigator.language.replace('-', '_')
export const i18n = createI18n({
  globalInjection: true, //全局生效$t
  legacy: false,
  locale: localStorage.getItem('lang') || browserLanguage || 'zh_CN',
  fallbackLocale: 'zh_CN',
  messages
})

export const { t } = i18n.global

export const langList: DropdownOption[] = [
  { content: '简体中文', value: 'zh_CN' },
  { content: 'English', value: 'en_US' }
]

export default i18n
