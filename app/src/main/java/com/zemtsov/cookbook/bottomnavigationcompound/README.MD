The following topics are covered

1. Custom views (compound controls)
https://developer.android.com/guide/topics/ui/custom-components?hl=en#compound
With XML layout (BottomNavigationView) and without XML layout (BottomNavigationBar)

2. Kotlin callbacks
var onItemSelectedListener: ((index: Int, item: BottomNavigationView) -> Unit)? = null
var onItemReselectedListener: ((index: Int, item: BottomNavigationView) -> Unit)? = null

3. Animation
...