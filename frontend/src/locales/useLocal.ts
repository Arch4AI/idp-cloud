import { useI18n } from 'vue-i18n'
import { computed } from 'vue'
import { LangName, i18n } from '@/locales/index'

export function useLocale() {
  const { locale } = useI18n({ useScope: 'global' })
  function changeLocale(lang: LangName) {
    locale.value = lang
    localStorage.setItem('lang', lang)
  }
  const getLocale = computed(() => {
    return i18n.global.getLocaleMessage(locale.value as string).tdesignLocale
  })
  return {
    changeLocale,
    getLocale
  }
}
