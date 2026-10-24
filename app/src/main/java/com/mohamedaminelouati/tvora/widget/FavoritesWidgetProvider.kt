package com.mohamedaminelouati.tvora.widget

import com.mohamedaminelouati.tvora.R

class FavoritesWidgetProvider : BaseWidgetProvider() {
    override val widgetType: Int = WidgetRemoteViewsService.TYPE_FAVORITES
    override val layoutResId: Int = R.layout.widget_favorites
}
