package com.mohamedaminelouati.tvora.widget

import com.mohamedaminelouati.tvora.R

class NowPlayingWidgetProvider : BaseWidgetProvider() {
    override val widgetType: Int = WidgetRemoteViewsService.TYPE_NOW_PLAYING
    override val layoutResId: Int = R.layout.widget_now_playing
}
