public class KotlinVisitor extends KotlinParserBaseVisitor<String>
{
    boolean inUIfunc = false;
    boolean makingButton = false;
    boolean makingText = false;
    boolean makingTextField = false;
    boolean makingColumn=false;
    @Override
    public String visitKotlinFile(KotlinParser.KotlinFileContext ctx) {
        String toplevelobjs = "";
        for (KotlinParser.TopLevelObjectContext ct : ctx.topLevelObject())
        {
            toplevelobjs += (visitTopLevelObject(ct));
        }
        return (visitPackageHeader(ctx.packageHeader()) + visitImportList(ctx.importList()) +toplevelobjs);
    }
    @Override
    public String visitPackageHeader(KotlinParser.PackageHeaderContext ctx) {
        //return super.visitPackageHeader(ctx);
        return "//no package header yet\n";
    }

    @Override
    public String visitImportList(KotlinParser.ImportListContext ctx) {
        //return super.visitImportList(ctx);
        return "//no imports yet\n";
    }

    @Override
    public String visitTopLevelObject(KotlinParser.TopLevelObjectContext ctx) {
        return (visitDeclaration(ctx.declaration()) + visitSemis(ctx.semis()));
    }

    @Override
    public String visitFunctionDeclaration(KotlinParser.FunctionDeclarationContext ctx) {
        if(visitModifiers(ctx.modifiers()).contains("@composable"))
        {
            inUIfunc = true;
            String out = "var body: some View \n VStack(alignment: .leading)\n" + visitFunctionBody(ctx.functionBody());
            inUIfunc = false;
            return out;
        }
        String returntype = "";
        if(ctx.type_() != null)
        {
            returntype =" -> " + visitType_(ctx.type_());
        }
        return (visitModifiers(ctx.modifiers()) + "func " + visitSimpleIdentifier(ctx.simpleIdentifier())
                + visitFunctionValueParameters(ctx.functionValueParameters()) + returntype
                + "\n" + visitFunctionBody(ctx.functionBody()));
    }

    @Override
    public String visitModifiers(KotlinParser.ModifiersContext ctx) {
        if(ctx != null)
        {
            String mods = "";
            for(KotlinParser.AnnotationContext ct : ctx.annotation())
            {
                mods += visitAnnotation(ct) + "\n";
            }
            return mods;
        }
        else{return "";}
    }

    @Override
    public String visitAnnotation(KotlinParser.AnnotationContext ctx) {
        return visitSingleAnnotation(ctx.singleAnnotation());
    }

    @Override
    public String visitSingleAnnotation(KotlinParser.SingleAnnotationContext ctx) {
        return "@" + visitUnescapedAnnotation(ctx.unescapedAnnotation());
    }

    @Override
    public String visitFunctionValueParameters(KotlinParser.FunctionValueParametersContext ctx) {
        String functionparameters = "";
        int parametersLength = ctx.functionValueParameter().size()-1;
        for (KotlinParser.FunctionValueParameterContext ct : ctx.functionValueParameter())
        {
            if(ct != ctx.functionValueParameter().get(parametersLength))
            {
                functionparameters += (visitFunctionValueParameter(ct)) +" , ";
            }
            else functionparameters += (visitFunctionValueParameter(ct));

        }
        return "(" + functionparameters +")";
    }

    @Override
    public String visitParameter(KotlinParser.ParameterContext ctx) {
        return (visitSimpleIdentifier(ctx.simpleIdentifier()) + ": " + visitType_(ctx.type_()));
    }

    @Override
    public String visitBlock(KotlinParser.BlockContext ctx) {
        return ("{\n\n" + visitStatements(ctx.statements()) + "\n}\n");
    }


    @Override
    public String visitStatements(KotlinParser.StatementsContext ctx) {
        String statements = "";
        for (KotlinParser.StatementContext ct : ctx.statement())
        {
            statements += (visitStatement(ct)) + "\n";
        }
        return statements;
    }


    @Override
    public String visitClassDeclaration(KotlinParser.ClassDeclarationContext ctx) {
        return ("class " + visitSimpleIdentifier(ctx.simpleIdentifier()) + "\n" + visitClassBody(ctx.classBody()));
    }

    @Override
    public String visitClassBody(KotlinParser.ClassBodyContext ctx) {
        if(ctx == null){return "";}
        return ("{\n\n" + visitClassMemberDeclarations(ctx.classMemberDeclarations()) + "\n}\n");
    }

    @Override
    public String visitClassMemberDeclarations(KotlinParser.ClassMemberDeclarationsContext ctx) {
        String myout = "";
        for (KotlinParser.ClassMemberDeclarationContext ct : ctx.classMemberDeclaration())
        {
            myout += (visitClassMemberDeclaration(ct));
        }
        return myout;
    }

    @Override
    public String visitSemis(KotlinParser.SemisContext ctx) {
        return "\n";
    }

    @Override
    public String visitPropertyDeclaration(KotlinParser.PropertyDeclarationContext ctx) {
        String myV = "";
        if(ctx.VAR() != null){myV = "var";}
        else{myV = "let";}
        return (myV + " " + visitVariableDeclaration(ctx.variableDeclaration()) + ctx.ASSIGNMENT().getText() + " "
                + visitExpression(ctx.expression()) +"\n");
    }

    @Override
    public String visitAssignment(KotlinParser.AssignmentContext ctx) {
        if(ctx.ASSIGNMENT() != null)
        {
            return (visitDirectlyAssignableExpression(ctx.directlyAssignableExpression()) + ctx.ASSIGNMENT().getText() + " " +
                    visitExpression(ctx.expression()));
        }
        else
        {
            return (visitAssignableExpression(ctx.assignableExpression())
                    + visitAssignmentAndOperator(ctx.assignmentAndOperator())+ " " +
                    visitExpression(ctx.expression()));
        }
    }

    @Override
    public String visitAssignmentAndOperator(KotlinParser.AssignmentAndOperatorContext ctx) {
        String myout = "";
        if(ctx.ADD_ASSIGNMENT() != null){myout = "+= ";}
        else if(ctx.SUB_ASSIGNMENT() != null){myout = "-= ";}
        else if(ctx.MULT_ASSIGNMENT() != null){myout = "*= ";}
        else if(ctx.DIV_ASSIGNMENT() != null){myout = "/= ";}
        else if(ctx.MOD_ASSIGNMENT() != null){myout = "%= ";}
        return myout;
    }

    @Override
    public String visitAdditiveExpression(KotlinParser.AdditiveExpressionContext ctx) {

        if(ctx.children.size() > 1)
        {
            String Statement = "";
            int statementsLenght = ctx.multiplicativeExpression().size() - 1;
            int i = 0;
            for (KotlinParser.MultiplicativeExpressionContext ct : ctx.multiplicativeExpression())
            {
                if(ct != ctx.multiplicativeExpression().get(statementsLenght))
                {
                    Statement += (visitMultiplicativeExpression(ct)) + visitAdditiveOperator(ctx.additiveOperator(i));
                    i++;
                }
                else Statement += (visitMultiplicativeExpression(ct));
            }
            return Statement;
        }
        else {return super.visitAdditiveExpression(ctx);}
    }

    @Override
    public String visitMultiplicativeExpression(KotlinParser.MultiplicativeExpressionContext ctx) {

        if(ctx.children.size() > 1)
        {
            String Statement = "";
            int statementsLenght = ctx.asExpression().size() - 1;
            int i = 0;
            for (KotlinParser.AsExpressionContext ct : ctx.asExpression())
            {
                if(ct != ctx.asExpression().get(statementsLenght))
                {
                    Statement += visitAsExpression(ct) + visitMultiplicativeOperator(ctx.multiplicativeOperator(i));
                    i++;
                }
                else Statement += (visitAsExpression(ct));
            }
            return Statement;
        }
        else {return super.visitMultiplicativeExpression(ctx);}

    }

    @Override
    public String visitAsExpression(KotlinParser.AsExpressionContext ctx) {
        if(ctx.children.size() > 1)
        {
            return visitPrefixUnaryExpression(ctx.prefixUnaryExpression()) + " as " + visitType_(ctx.type_());
        }
        else{return super.visitAsExpression(ctx);}
    }

    @Override
    public String visitIfExpression(KotlinParser.IfExpressionContext ctx) {
        if(ctx.children.size() > 1)
        {
            if(ctx.ELSE() != null)
            {
                return ("if (" + visitExpression(ctx.expression()) + ")\n"
                        + visitControlStructureBody(ctx.controlStructureBody(0)) + "\nelse\n"
                        + visitControlStructureBody(ctx.controlStructureBody(1)) );
            }
            else
            {
                return ("if (" + visitExpression(ctx.expression()) + " )\n"
                        + visitControlStructureBody(ctx.controlStructureBody(0)) );
            }

        }
        else {return super.visitIfExpression(ctx);}
    }

    @Override
    public String visitForStatement(KotlinParser.ForStatementContext ctx) {
        return ("for ( " + visitVariableDeclaration(ctx.variableDeclaration()) + " in " + visitExpression(ctx.expression()) + ")\n"
                + visitControlStructureBody(ctx.controlStructureBody()));
    }

    @Override
    public String visitRangeExpression(KotlinParser.RangeExpressionContext ctx) {
        if(ctx.children.size() > 1)
        {
            return (visitAdditiveExpression(ctx.additiveExpression(0)) +
                    "... " + visitAdditiveExpression(ctx.additiveExpression(1) ));
        }
        return super.visitRangeExpression(ctx);
    }

    @Override
    public String visitWhileStatement(KotlinParser.WhileStatementContext ctx) {
        return ("while ( " + visitExpression(ctx.expression()) +" )\n" + visitControlStructureBody(ctx.controlStructureBody()));
    }

    @Override
    public String visitDoWhileStatement(KotlinParser.DoWhileStatementContext ctx) {
        return ("repeat \n" +visitControlStructureBody(ctx.controlStructureBody())
                + "while ( " + visitExpression(ctx.expression()) + " )\n" );
    }

    @Override
    public String visitWhenExpression(KotlinParser.WhenExpressionContext ctx) {
        String entries = "";
        for(KotlinParser.WhenEntryContext ct : ctx.whenEntry())
        {
            entries += visitWhenEntry(ct) + "\n";
        }
        return("switch ( " + visitExpression(ctx.expression()) + " )" + "\n{\n" + entries + "}\n");
    }

    @Override
    public String visitWhenEntry(KotlinParser.WhenEntryContext ctx) {
        if(ctx.ELSE() != null)
        {
            return ("default" + ":\n" + visitControlStructureBody(ctx.controlStructureBody()));
        }
        String conditions = "";
        int conditionsSize =  ctx.whenCondition().size() - 1;
        for(KotlinParser.WhenConditionContext ct : ctx.whenCondition())
        {
            if(ct != ctx.whenCondition(conditionsSize))
            {
                conditions += visitWhenCondition(ct) + " , ";
            }
            else {conditions += visitWhenCondition(ct);}

        }
        return ("case " +conditions + ":\n" + visitControlStructureBody(ctx.controlStructureBody()));
    }

    @Override
    public String visitDisjunction(KotlinParser.DisjunctionContext ctx) {
        if(ctx.children.size() > 1)
        {
            String Statement = "";
            int statementsLenght = ctx.conjunction().size() - 1;
            for (KotlinParser.ConjunctionContext ct : ctx.conjunction())
            {
                if(ct != ctx.conjunction().get(statementsLenght))
                {
                    Statement += visitConjunction(ct) + " || ";
                }
                else {Statement += visitConjunction(ct);}
            }
            return Statement;
        }
        else{return super.visitDisjunction(ctx);}
    }

    @Override
    public String visitConjunction(KotlinParser.ConjunctionContext ctx) {
        if(ctx.children.size() > 1)
        {
            String Statement = "";
            int statementsLenght = ctx.equality().size() - 1;
            for (KotlinParser.EqualityContext ct : ctx.equality())
            {
                if(ct != ctx.equality().get(statementsLenght))
                {
                    Statement += visitEquality(ct) + " && ";
                }
                else {Statement += visitEquality(ct);}
            }
            return Statement;
        }
        else{return super.visitConjunction(ctx);}
    }

    @Override
    public String visitEquality(KotlinParser.EqualityContext ctx) {
        if(ctx.children.size() > 1)
        {
            return (visitComparison(ctx.comparison(0)) + " " + visitEqualityOperator(ctx.equalityOperator(0))
                    + " " + visitComparison(ctx.comparison(1)));
        }
        else{ return super.visitEquality(ctx);}
    }

    @Override
    public String visitComparison(KotlinParser.ComparisonContext ctx) {
        if(ctx.children.size() > 1)
        {
            return (visitInfixOperation(ctx.infixOperation(0)) + " " + visitComparisonOperator(ctx.comparisonOperator())
                    + " " + visitInfixOperation(ctx.infixOperation(1)));
        }
        else{ return super.visitComparison(ctx);}
    }

    @Override
    public String visitPostfixUnaryExpression(KotlinParser.PostfixUnaryExpressionContext ctx) {
        String postfixsuffix = "";
        for (KotlinParser.PostfixUnarySuffixContext ct : ctx.postfixUnarySuffix())
        {
            postfixsuffix += visitPostfixUnarySuffix(ct);
        }
        if(visitPrimaryExpression(ctx.primaryExpression()) != null)
        {
            if(visitPrimaryExpression(ctx.primaryExpression()).contains("TextField"))
            {

                makingTextField = true;
                for (KotlinParser.PostfixUnarySuffixContext ct : ctx.postfixUnarySuffix())
                {
                    postfixsuffix += visitPostfixUnarySuffix(ct);
                }
                String out = "@state var value: String  = \"value\" \n"
                        + visitPrimaryExpression(ctx.primaryExpression()) + postfixsuffix;
                makingTextField = false;
                return out;
            }
            if(visitPrimaryExpression(ctx.primaryExpression()).contains("Column"))
            {
                for (KotlinParser.PostfixUnarySuffixContext ct : ctx.postfixUnarySuffix())
                {
                    postfixsuffix += visitPostfixUnarySuffix(ct);
                }

                String out = "VStack (alignment: .center, spacing: 20)"+ postfixsuffix ;
                return out;
            }
            if(visitPrimaryExpression(ctx.primaryExpression()).contains("Row"))
            {

                for (KotlinParser.PostfixUnarySuffixContext ct : ctx.postfixUnarySuffix())
                {
                    postfixsuffix += visitPostfixUnarySuffix(ct);
                }
                String out = "HStack (alignment: .center, spacing: 20)" + postfixsuffix ;
                return out;
            }
            if(visitPrimaryExpression(ctx.primaryExpression()).contains("Button"))
            {
                makingButton = true;
                for (KotlinParser.PostfixUnarySuffixContext ct : ctx.postfixUnarySuffix())
                {
                    postfixsuffix += visitPostfixUnarySuffix(ct);
                }
                String out = (visitPrimaryExpression(ctx.primaryExpression()) + " action:" + postfixsuffix);
                makingButton = false;
                return out;
            }
            for (KotlinParser.PostfixUnarySuffixContext ct : ctx.postfixUnarySuffix())
            {
                postfixsuffix += visitPostfixUnarySuffix(ct);
            }
            String out = (visitPrimaryExpression(ctx.primaryExpression()) + " " + postfixsuffix);
            return out;
        }
        return postfixsuffix;
    }

    @Override
    public String visitAnnotatedLambda(KotlinParser.AnnotatedLambdaContext ctx) {
        if(ctx.children.size() > 1)
        {
            if(makingButton)
            {
                return "\n label:" + visitLambdaLiteral(ctx.lambdaLiteral());
            }
            return "\n" + visitLambdaLiteral(ctx.lambdaLiteral());
        }
        else{return super.visitAnnotatedLambda(ctx);}
    }

    @Override
    public String visitLambdaLiteral(KotlinParser.LambdaLiteralContext ctx) {
        return "{\n\n" + visitStatements(ctx.statements()) + "\n}";
    }

    @Override
    public String visitNavigationSuffix(KotlinParser.NavigationSuffixContext ctx) {
        if(ctx.children.size() > 1)
        {
            return visitMemberAccessOperator(ctx.memberAccessOperator()) + visitSimpleIdentifier(ctx.simpleIdentifier());
        }
        else{return super.visitNavigationSuffix(ctx);}
    }

    @Override
    public String visitMemberAccessOperator(KotlinParser.MemberAccessOperatorContext ctx) {
        return ".";
    }

    @Override
    public String visitTypeArguments(KotlinParser.TypeArgumentsContext ctx) {
        String types = "";
        int typesSize = ctx.typeProjection().size() - 1;
        for (KotlinParser.TypeProjectionContext ct : ctx.typeProjection())
        {
            if(ct != ctx.typeProjection(typesSize))
            {
                types += visitTypeProjection(ct) + ",";
            }
            else {types += visitTypeProjection(ct);}
        }
        return ("< " + types + " >");
    }

    @Override
    public String visitValueArguments(KotlinParser.ValueArgumentsContext ctx) {
        String values = "";
        int typesSize = ctx.valueArgument().size() - 1;
        for (KotlinParser.ValueArgumentContext ct : ctx.valueArgument())
        {
            if(ct != ctx.valueArgument(typesSize))
            {
                values += visitValueArgument(ct) + ", ";
            }
            else {values += visitValueArgument(ct);}
        }
        if(makingButton)
        {
            if(!makingText)
            {
                return values;
            }
            else{ makingText = false; }
        }
        if(makingTextField)
        {
            String Placeholdertext = "";
            for (KotlinParser.ValueArgumentContext ct : ctx.valueArgument())
            {
                String value = visitValueArgument(ct);
                if(value.contains("placeholder"))
                {
                    int index = value.indexOf("\"");
                    if(index!=0)
                    {
                        int index2 = value.indexOf("\"" , value.indexOf("\"")+1);
                        value = value.substring(index,index2+1);
                        System.out.println(value);
                    }
                    Placeholdertext = value;
                    return ("(" + Placeholdertext + "," + "text: $value" + ")");
                }
            }
        }
        return ("( " + values + " )");
    }

    @Override
    public String visitValueArgument(KotlinParser.ValueArgumentContext ctx)
    {
        if(ctx.simpleIdentifier()!= null)
        {
            if(visitSimpleIdentifier(ctx.simpleIdentifier()).contains("onClick"))
            {
                if(inUIfunc)
                {
                    return visitExpression(ctx.expression());
                }
            }
            if(visitSimpleIdentifier(ctx.simpleIdentifier()).contains("text"))
            {
                if(inUIfunc)
                {
                    makingText = true;
                    String out = visitExpression(ctx.expression());
                    return out;
                }
            }
        }
        if(ctx.children.size() > 1)
        {
            return visitSimpleIdentifier(ctx.simpleIdentifier()) + ctx.ASSIGNMENT().getText() + " " + visitExpression(ctx.expression());
        }
        else{return super.visitValueArgument(ctx);}
    }

    @Override
    public String visitCallSuffix(KotlinParser.CallSuffixContext ctx) {
        if(ctx.children.size() > 1)
        {
            return visitValueArguments(ctx.valueArguments()) + visitAnnotatedLambda(ctx.annotatedLambda());
        }
        else{return super.visitCallSuffix(ctx);}
    }

    @Override
    public String visitJumpExpression(KotlinParser.JumpExpressionContext ctx) {
        if(ctx.CONTINUE() != null){return  "continue";}
        else if(ctx.BREAK() != null) {return "break";}
        else
        {
            String expression;
            if(ctx.expression()!= null)
            {
                expression = visitExpression(ctx.expression());
                return (ctx.RETURN().getText() + " " + expression);
            }
            else {return (ctx.RETURN().getText());}
        }
    }

    @Override
    public String visitEqualityOperator(KotlinParser.EqualityOperatorContext ctx) {
        String myout = "";
        if(ctx.EXCL_EQ() != null) { myout =  "!=";}
        else if (ctx.EXCL_EQEQ() != null) {myout =  "!==";}
        else if (ctx.EQEQ() != null) {myout =  "==";}
        else {myout =  "===";}
        return myout;
    }

    @Override
    public String visitComparisonOperator(KotlinParser.ComparisonOperatorContext ctx) {
        String myout = "";
        if(ctx.LANGLE() != null) { myout =  "<";}
        else if (ctx.LE() != null) {myout =  "<=";}
        else if (ctx.RANGLE() != null) {myout =  ">";}
        else {myout =  ">=";}
        return myout;
    }

    @Override
    public String visitAdditiveOperator(KotlinParser.AdditiveOperatorContext ctx) {
        String myout = "";
        if(ctx.ADD()!= null) {myout = "+ ";}
        else{myout = "- ";}
        return myout;
    }

    @Override
    public String visitMultiplicativeOperator(KotlinParser.MultiplicativeOperatorContext ctx) {
        String myout = "";
        if(ctx.MULT()!= null) {myout = "* ";}
        else if (ctx.MOD() != null){myout = "% ";}
        else {myout = "/ ";}
        return myout;
    }

    @Override
    public String visitLiteralConstant(KotlinParser.LiteralConstantContext ctx) {
        if(ctx.IntegerLiteral() != null){return (ctx.IntegerLiteral().getText() + " ");}
        else if(ctx.BooleanLiteral() != null){return (ctx.BooleanLiteral().getText() + " ");}
        else if(ctx.LongLiteral() != null){return (ctx.LongLiteral().getText() + " ");}
        else if(ctx.RealLiteral() != null){return (ctx.RealLiteral().getText() + " ");}
        else if(ctx.BinLiteral() != null){return (ctx.BinLiteral().getText() + " ");}
        else if(ctx.HexLiteral() != null){return (ctx.HexLiteral().getText() + " ");}
        else if(ctx.CharacterLiteral() != null){return (ctx.CharacterLiteral().getText() + " ");}
        else {return (ctx.NullLiteral().getText() + " ");}
    }

    @Override
    public String visitLineStringLiteral(KotlinParser.LineStringLiteralContext ctx) {
        if(ctx.children.size() > 1)
        {
            String linestring = "";
            for (KotlinParser.LineStringContentContext ct:ctx.lineStringContent())
            {
                linestring += visitLineStringContent(ct);
            }
            return ctx.QUOTE_OPEN().getText() + linestring + ctx.QUOTE_CLOSE().getText();
        }
        else{return super.visitLineStringLiteral(ctx);}
    }

    @Override
    public String visitLineStringContent(KotlinParser.LineStringContentContext ctx) {
        if(ctx.LineStrText() == null)
        {
            return "";
        }
        return ctx.LineStrText().getText();
    }

    @Override
    public String visitSimpleIdentifier(KotlinParser.SimpleIdentifierContext ctx) {
        if(ctx.Identifier() == null){return "";}
        return ctx.Identifier().getText() + " ";
    }
}
