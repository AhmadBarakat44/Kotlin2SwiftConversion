//no package header yet
//no imports yet
class TextTest 
{
    var body: some View
     VStack(alignment: .leading)
    {
        @state var value: String  = "value"
        TextField ("placeholder",text: $value)
    }
}

