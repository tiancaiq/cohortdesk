import { useEffect, useRef } from 'react'
import * as echarts from 'echarts/core'
import { PieChart, BarChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([PieChart, BarChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

export const chartText = { color: '#a9c4d4', fontSize: 12 }
export const chartColors = ['#58d5e6', '#8c8bfa', '#60d6ad', '#edb76c', '#f0859e', '#8bb5e8']

export function Chart({ option, className = '' }) {
  const element = useRef(null)
  const chart = useRef(null)
  useEffect(() => {
    if (!element.current) return
    chart.current = echarts.init(element.current)
    const resize = () => chart.current?.resize()
    window.addEventListener('resize', resize)
    return () => { window.removeEventListener('resize', resize); chart.current?.dispose(); chart.current = null }
  }, [])
  useEffect(() => { if (chart.current && option) chart.current.setOption(option, true) }, [option])
  return <div ref={element} className={`chart ${className}`} role="img" aria-label="Data chart" />
}
