package me.aaronfang.demos.component_ui

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.aaronfang.demos.R

@Composable
fun MyText(){
//    var color = Color.Black

    // 表示状态 等待事件向上流动，修改状态。然后状态向下流动去修改颜色
    // 类似于MvRx的State
    val nameState: MutableState<Color> = remember {
        mutableStateOf(Color.Blue)
    }

    Text(
        text = "Hello World",
        color = nameState.value,
        // 边框
        modifier = Modifier
            .border(
                border = BorderStroke(
                    width = 3.dp,
                    color = Color(0xFF9999999)
                ),
                shape = RoundedCornerShape(20f, 60f, 20f, 160f)
            )
            .clickable(
                // 按照Flutter思维，这样修改是不起效的，"状态"没有改变，或者没有"重绘"
                // onClick = {color = Color.Red}
                onClick = { nameState.value = Color.Red },
                // Beta 版本没有 双击？
            ),
        // 字体大小
        fontSize = 33.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        textDecoration = TextDecoration.LineThrough
    )
}

@Preview(name = "study Image")
@Composable
fun StudyImageView() {
    val daggerOffset: MutableState<Float> = remember {
        mutableStateOf(0f)
    }
    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.qwer)
    Image(
        bitmap =  imageBitmap,
        contentDescription = "123123",
        modifier = Modifier.ImageModifier(daggerOffset)
    )
}

/**
 * 自定义裁剪
 */
@Stable
val RectangleImageShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        path.moveTo(0f, 0f)
        path.relativeLineTo(20f, 20f)
        path.relativeCubicTo(40f, 40f, 60f, 60f, -20f, 130f)
        path.relativeCubicTo(40f, 40f, 60f, 60f, -20f, 130f)
        path.relativeCubicTo(40f, 40f, 60f, 60f, -20f, 130f)
        path.relativeCubicTo(40f, 40f, 60f, 60f, -20f, 130f)
        path.relativeCubicTo(40f, 40f, 60f, 60f, -20f, 130f)
        path.relativeCubicTo(40f, 40f, 60f, 60f, -20f, 130f)
        path.lineTo(size.width, size.height)
        path.lineTo(size.width, 0f)
        path.close()
        return Outline.Generic(path)
    }
}


private fun Modifier.ImageModifier(daggerOffset: MutableState<Float>):Modifier =
    composed {
        Modifier
            .height(260.dp)
            .width(200.dp)
            .padding(horizontal = 30.dp, vertical = 30.dp)
            .clip(
                RectangleImageShape
            )
            .rotate(10f)
            .draggable(
                state = DraggableState(onDelta = {
                    daggerOffset.value = +it
                    Log.e("onDelta", "StudyImageView: " + daggerOffset.value)
                }),
                orientation = Orientation.Horizontal
            )
            .offset(x = daggerOffset.value.dp)
    }


@Preview(name = "row_demo")
@Composable
fun StudyLayoutViews(){
    // 列
    Column {
        // 行
        Row {
            Text(text = "第一列",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.background(Color.Red))
            Text(text = "第二列",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.background(Color.Green))
            Text(text = "第三列",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.background(Color.Blue))
        }
        Row {
            Text(text = "第一列",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.background(Color.Red))
            Text(text = "第二列",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.background(Color.Green))
        }
        Row {
            Text(text = "第一列",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.background(Color.Red))
        }
    }
}



//@Preview(name = "study_demo")
@Composable
fun StudyDemo(onClick: () -> Unit) {
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.errorggl)
    val deleteIcon = ImageBitmap.imageResource(id = R.drawable.demo)
    val context = LocalContext.current
    Box(modifier = Modifier
        .clickable(onClick = onClick)
        .clip(BoxClipShapes)
        .background(Color(206, 236, 250))
        .padding(10.dp)
    )
    {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "sd",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .background(Color.White, shape = CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .shadow(elevation = 150.dp, clip = true)
            )
            Column(modifier = Modifier.padding(start = 5.dp)) {
                Text(
                    text = "container",
                    fontSize = 16.sp ,
                    color = Color.Black
                )
                Text(
                    text = "容器组件",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "123w播放量",
                    color = Color.White,
                    fontSize = 8.sp)
            }
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .padding(start = 30.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(bitmap = deleteIcon,
                    contentDescription = "delete",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp)
                        .shadow(elevation = 150.dp, clip = true)
                )
            }
        }
    }
}

@Stable
val BoxClipShapes: Shape = object : Shape{
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        path.moveTo(20f, 0f)
        path.relativeLineTo(-20f, 20f)
        path.relativeLineTo(0f,size.height - 40f)
        path.relativeLineTo(20f, 20f)
        path.relativeLineTo(size.width / 3f-20, 0f)
        path.relativeLineTo(15f, -20f)
        path.relativeLineTo(size.width / 3f-30, 0f)
        path.relativeLineTo(15f, 20f)
        path.relativeLineTo(size.width / 3f-20, 0f)
        path.relativeLineTo(20f, -20f)
        path.relativeLineTo(0f, -(size.height - 40f))
        path.relativeLineTo(-20f, -20f)
        path.relativeLineTo(-(size.width / 3f-20),0f)
        path.relativeLineTo(-15f,20f)
        path.relativeLineTo(-(size.width / 3f-30), 0f)
        path.relativeLineTo(-15f, -20f)
        path.close()

        return Outline.Generic(path)
    }
}


//@Preview(name = "scroll_learn")
@Composable
fun ListStudy(onClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val scrollLazyState = rememberLazyListState()
    LazyColumn(state = scrollLazyState) {
        items(100)  {
            Row(modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
                StudyDemo(onClick = onClick)
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
//                StudyDemo(onClick = onClick)
            }
        }
    }
}


@Stable
class QueryToImageShapes(var radian: Float = 100f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(0f, size.height - radian)
        path.quadraticBezierTo(size.width / 2, size.height, size.width, size.height - radian)
        path.lineTo(size.width, 0f)
        path.close()
        return Outline.Generic(path = path)
    }
}


@Preview(name = "login")
@Composable
fun LoginPage() {
    val bitmap: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.errorggl)
    Box(contentAlignment = Alignment.Center) {
        // 顶端图片
        Image(
            bitmap = bitmap,
            contentDescription = "bitmap",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(QueryToImageShapes(160f))
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(0.dp)
                .clip(CircleShape)
                .background(Color(206, 236, 250, 121))
                .width(130.dp)
                .height(130.dp)
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = "w",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .background(color = Color(0xFF0DBEBF), shape = CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .shadow(elevation = 150.dp, clip = true)
            )
        }


    }
}


@Preview(name ="login_text_hint" )
@Composable
fun LoginTextHints() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Login", fontSize = 18.sp)
            Text(text = "Everything about Jetpack Compose", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Preview(name ="login_text_field" )
@Composable
fun LoginTextField() {
    val deleteIcon: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.demo)

    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxWidth()
        .padding(top = 30.dp))
    {
        TextField(
            value = "   ConmposeUnit",
            onValueChange = { },
            shape = RoundedCornerShape(18.dp),
            colors = textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.White

            ),
            modifier = Modifier.border(
                1.dp,
                Color(111, 111, 111, 66),
                shape = RoundedCornerShape(18.dp)
            ),
            leadingIcon = { Icon(bitmap = deleteIcon, contentDescription = "") })
    }
}







