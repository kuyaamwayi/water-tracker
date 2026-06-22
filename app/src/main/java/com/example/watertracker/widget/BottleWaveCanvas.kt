package com.example.watertracker.widget

// BottleWaveCanvas.kt — REMOVED FROM WIDGET USE
//
// The drawWaterBottle(fillRatio) function using DrawScope/Canvas CANNOT be used
// inside a Glance AppWidget. Glance widgets render via Android RemoteViews, which
// is a serialized XML layout system — it does not support arbitrary Canvas drawing.
//
// Options if you want the bottle visual:
//
// Option A (Recommended): Pre-render the bottle states as PNG drawables.
//   Generate 5 images (0%, 25%, 50%, 75%, 100% fill) and swap them in the widget
//   using ImageProvider(R.drawable.bottle_fill_XX) based on fillRatio.
//
// Option B: Use this Canvas code inside MainActivity only (Compose screen),
//   not the widget. The widget shows the progress bar instead.
//
// Option C: Use a custom RemoteViews with a real Android View that does Canvas
//   drawing, then wrap it via Glance's AndroidRemoteViews composable.
//   This is complex but achieves the visual.
//
// For now the widget uses a simple fill bar which works reliably across all
// Android versions and widget hosts.
