class s
{
    var x = 5
    var y = 9
    fun main()
    {
        x = x * 2 * 8 + y - 3 / 4
        class buttonTest
        {
            @composable
            fun myUI()
            {
                Button(onClick = {
                    val x = 2 + x
                })
                {
                    Text(text = "click me")
                }
            }
        }
    }
    fun test(a:Int , b:Int) : Int
    {
        return 1
    }
}
class u
{
    var t = 0
    var z = 0
    fun myfun()
    {
        if (2 !== 3 || 4 != 3 && 5 < 3 && 8 > 3 || 5 >= 3)
        {
            for (i in 5..10)
            {
                while(z>=2)
                {
                    z +=  t
                }
            }
            z %= t
        }
        controltest()
        return
    }
    fun controltest()
    {
        var x = 0
        var y = 9
        for (item in 1..18)
        {

        }
        while (x <=2)
        {
            x *= 2
        }
        when (x)
        {
            1,5 -> x = x - 2
            2 -> x = x + 4
            7 -> if(y > 8){x = x - 12*2}
            else -> {
                x = x + 8
            }
        }
    }
    @composable
    fun myUI() {
        TextField(
                label = { Text("label") },
                placeholder = { Text("placeholder") }
        )
        Button(onClick =
        {
            val x = 5
        })
        {
            Text(text = "click me")
        }
        Text(text = "simple text")
    }

}