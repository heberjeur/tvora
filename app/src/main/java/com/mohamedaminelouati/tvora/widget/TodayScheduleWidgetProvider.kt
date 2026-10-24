package com.mohamedaminelouati.tvora.widget

import com.mohamedaminelouati.tvora.R

class TodayScheduleWidgetProvider : BaseWidgetProvider() {
    override val widgetType: Int = WidgetRemoteViewsService.TYPE_TODAY
    override val layoutResId: Int = R.layout.widget_today
}
