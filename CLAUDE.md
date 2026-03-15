# Rebow Project Rules

## Tech Stack
- Expo SDK 55, React Native 0.83.2 (New Architecture)
- expo-router (file-based routing)
- TypeScript, i18next, react-native-reanimated
- Fonts: Outfit, Inter, DMSans, BricolageGrotesque

## Bottom Sheet Rules

All bottom sheets in this project MUST follow these rules:

### Layout
- `minHeight: '70%'`, `maxHeight: '94%'` on the sheet container
- Sheet has `borderTopLeftRadius: 24`, `borderTopRightRadius: 24`
- `paddingBottom: Math.max(insets.bottom, 40)` for safe area

### Handle Bar (Drag-to-dismiss)
- Gray handle bar at top of sheet inside a `dragArea` with `paddingVertical: 8`
- Attach `PanResponder` to the drag area
- Drag down 120px+ OR fast swipe (`vy > 0.5`) closes the sheet
- During drag, sheet follows finger (`sheetAnim.setValue(g.dy)`) and overlay fades proportionally
- If drag is insufficient, sheet snaps back with animation

### Save Button
- Save button is OUTSIDE the ScrollView, fixed at the bottom of the sheet
- Keyboard can naturally cover the save button — this is intentional

### Form Content
- Form fields are inside a `KeyboardAwareScrollView` (from `react-native-keyboard-controller`) with `flex: 1`
- `keyboardShouldPersistTaps="handled"` and `bottomOffset={80}` on the scroll view
- This automatically scrolls focused inputs into view when keyboard opens
- `KeyboardProvider` wraps the app in `_layout.tsx`

### Keyboard Behavior
- NO auto-focus on any input when sheet opens
- Sheet View uses `onStartShouldSetResponder={(e) => e.target === e.currentTarget}` + `onResponderRelease={Keyboard.dismiss}` so tapping empty areas dismisses keyboard (do NOT use `TouchableWithoutFeedback` wrapper as it intercepts TextInput touches)
- `pointerEvents="box-none"` on the sheet container so touches pass through to the dim overlay behind

### Dim Overlay Behavior
- If keyboard is visible: tapping dim area dismisses keyboard only (sheet stays open)
- If keyboard is NOT visible: tapping dim area closes the sheet
- Use `Keyboard.isVisible()` to determine which action to take

### X Button
- Always present in the sheet header
- Closes the sheet (with animation)

### Animation
- Open: slide up from bottom (300ms) + overlay fade in
- Close: slide down (250ms) + overlay fade out
- Snap-back on insufficient drag: 200ms

## General
- This is a React Native mobile app. Preview tools do NOT apply.
- CocoaPods requires `export LANG=en_US.UTF-8` due to Korean path characters.
- Metro bundler hot-reloads changes to the device automatically.
