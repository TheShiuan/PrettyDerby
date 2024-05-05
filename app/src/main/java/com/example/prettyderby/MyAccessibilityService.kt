package com.example.prettyderby

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.graphics.Path
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {
    val TAG = javaClass.simpleName

    private var isClicked=false
    private val clickDelay = 5000L

    private val clickHandler = Handler(Looper.getMainLooper())
    private val clickRunnable = Runnable {
        val rootNode = rootInActiveWindow
        performClick(rootNode)
        Log.d(TAG, "Delay!")
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName= event?.packageName
        Log.d(TAG, "onAccessibilityEvent：$event")

        // 在这里处理辅助功能事件
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED!=event?.eventType){
            // 设置新的延迟任务
            if (isClicked)
                return
            clickHandler.postDelayed({clickButton("com.example.prettyderby:id/Bt_longin")},clickDelay)
            clickHandler.removeCallbacks(clickRunnable)
            isClicked=true
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
        // onInterrupt 不应使用 event 参数
        // 你可以在这里处理一些中断时的逻辑
    }

    private fun performClick(nodeInfo: AccessibilityNodeInfo?) {
        if (nodeInfo == null) {
            Log.d(TAG, "NodeInfo is null")
            return
        }
        Log.d(TAG, "Button ID: ${nodeInfo.viewIdResourceName}")
        print(nodeInfo.childCount)

        // 递归处理子节点
        for (i in 0 until nodeInfo.childCount) {
            performClick(nodeInfo.getChild(i))
            print(nodeInfo.getChild(i).className)
        }
        /*if(!isClicked){
            clickPos(this,70f,390f)
        }*/
    }

    private fun clickButton(string: String){
        val rootNode=rootInActiveWindow
        if (rootNode!=null){
            val nodes=rootNode.findAccessibilityNodeInfosByViewId(string)
            if (nodes!=null&&nodes.isNotEmpty()){
                val buttonNode = nodes[0]
                buttonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    private fun clickPos(accessibilityService: AccessibilityService, x: Float, y: Float) {
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(x, y)
        path.lineTo(x, y)
        builder.addStroke(GestureDescription.StrokeDescription(path, 0, 1))
        val gesture = builder.build()

        accessibilityService.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription) {
                    super.onCancelled(gestureDescription)
                    println("onCancelled")
                }

                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)
                    println("onCompleted")
                }
            },
            null
        )
    }

}
