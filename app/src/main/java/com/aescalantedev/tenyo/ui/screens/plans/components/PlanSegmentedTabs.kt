package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary

/**
 * Pestañas minimalistas con barra indicadora inferior idénticas a la captura de referencia.
 *
 * Responsabilidad única:
 * - Renderiza "Mis rutinas" y "Explorar catálogo" con línea inferior activa en lila (#9B83F5).
 */
@Composable
fun PlanUnderlineTabs(
    selectedTabIndex: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pestaña 0: Mis rutinas
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onSelectTab(0) }
                ),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Mis rutinas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 17.sp
                ),
                color = if (selectedTabIndex == 0) HtmlAppPrimary else TenyoTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(82.dp)
                    .height(2.5.dp)
                    .background(
                        color = if (selectedTabIndex == 0) HtmlAppPrimary else Color.Transparent,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        // Pestaña 1: Explorar catálogo
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onSelectTab(1) }
                ),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Explorar catálogo",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 17.sp
                ),
                color = if (selectedTabIndex == 1) HtmlAppPrimary else TenyoTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(124.dp)
                    .height(2.5.dp)
                    .background(
                        color = if (selectedTabIndex == 1) HtmlAppPrimary else Color.Transparent,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/**
 * Compatibilidad con pestañas segmentadas tipo cápsula.
 */
@Composable
fun SegmentedTabOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) TenyoJadePrimary else Color.Transparent
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                ),
                color = if (selected) TenyoOnJadePrimary else TenyoTextSecondary
            )
        }
    }
}
