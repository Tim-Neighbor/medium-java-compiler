Package FinalProject;

Helpers
	anychar = [35..255];
	digit = ['0'..'9'];
	letter =  [['a'..'z'] + ['A'..'Z']];
	cr = 13;
	lf = 10;
	tab = 9;
	sp = ' ' | cr | lf | tab;
    
Tokens
	string = '"' anychar+ '"';
	
	begin = 'BEGIN';
	end = 'END';
	classkeyword = 'CLASS';
	if = 'IF';
	then = 'THEN';
	else = 'ELSE';
	while = 'WHILE';
	for = 'FOR';
	get = 'GET';
	put = 'PUT';
	new = 'NEW';
	return = 'RETURN';
	switch = 'SWITCH';
	case = 'CASE';
	break = 'BREAK';
	default = 'DEFAULT';
	true = 'TRUE';
	false = 'FALSE';
	inttype = 'INT';
	realtype = 'REAL';
	stringtype = 'STRING';
	booleantype = 'BOOLEAN';
	voidtype = 'VOID';
	
	assign = ':=';
	equals = '==';
	notequals = '!=';
	greaterorequals = '>=';
	lessorequals = '<=';
	greaterthan = '>';
	lessthan = '<';
	increment = '++';
	decrement = '--';
	plus = '+';
	minus = '-';
	times = '*';
	divide = '/';
	
	lbrace = '{';
	rbrace = '}';
	lbracket = '[';
	rbracket = ']';
	lparen = '(';
	rparen = ')';
	comma = ',';
	period = '.';
	semicolon = ';';
	colon = ':';
	
    id = letter (letter | digit | '_')*;
	int = digit digit*;
	real = digit+ '.' digit+;
    whitespace = sp+;
	
Ignored Tokens
    whitespace;

Productions
    prog = begin classmethodstmts end;
	
	classmethodstmts = {fullproduction} classmethodstmts classmethodstmt |
		{emptyproduction};
	
	classmethodstmt = {classdecl} classkeyword id lbrace methodstmtseqs rbrace |
		{methoddecl} type id lparen varlist rparen lbrace stmtseq rbrace |
		{fielddecl} id additionalidlist* colon type semicolon;
	
	methodstmtseqs = {fullproduction} methodstmtseqs methodstmtseq |
		{emptyproduction};
	
	methodstmtseq = {methoddecl} type id lparen varlist rparen lbrace stmtseq rbrace |
		{fielddecl} id additionalidlist* colon type semicolon;
	
	stmtseq = {fullproduction} stmt stmtseq |
		{emptyproduction};
	
	stmt = {assignexpr} id bracketedint? assign expr semicolon |
		{assignstring} id bracketedint? assign string semicolon |
		{vardecl} id additionalidlist* colon type bracketedint? semicolon |
		{if} if lparen boolean rparen then lbrace stmtseq rbrace |
		{ifelse} if lparen boolean rparen then [thenlbrace]:lbrace
			[thenstmtseq]:stmtseq [thenrbrace]:rbrace else [elselbrace]:lbrace
			[elsestmtseq]:stmtseq [elserbrace]:rbrace |
		{while} while lparen boolean rparen lbrace stmtseq rbrace |
		{for} for lparen type? id assign expr [firstsemicolon]:semicolon
			boolean [secondsemicolon]:semicolon forincrementer rparen lbrace
			stmtseq rbrace |
		{get} id bracketedint? assign get lparen rparen semicolon |
		{put} put lparen id bracketedint? rparen semicolon |
		{incr} id bracketedint? increment semicolon |
		{decr} id bracketedint? decrement semicolon |
		{new} [firstid]:id bracketedint? assign new [secondid]:id lparen rparen semicolon |
		{methodcall} id lparen varlisttwo rparen semicolon |
		{methodcallonsomething} [firstid]:id bracketedint? period [secondid]:id
			lparen varlisttwo rparen additionalmethodcall* semicolon |
		{return} return expr semicolon |
		{switch} switch [firstlparen]:lparen expr [firstrparen]:rparen lbrace
			case [secondlparen]:lparen int [secondrparen]:rparen
			[firstcolon]:colon [firststmtseq]:stmtseq breakstmt? casestmt*
			default [secondcolon]:colon [secondstmtseq]:stmtseq rbrace;
	
	additionalmethodcall = period id lparen varlisttwo rparen;
	
	forincrementer = {incr} id increment |
		{decr} id decrement |
		{assign} id assign expr;
	
	breakstmt = break semicolon;
	
	casestmt = case lparen int rparen colon stmtseq breakstmt?;
	
	varlist = varlisthelper?;
	
	varlisthelper = id colon type bracketedint? additionalvarlist*;
	
	additionalvarlist = comma id colon type bracketedint?;
	
	varlisttwo = varlisttwohelper?;
	
	varlisttwohelper = expr additionalexprlist*;
	
	additionalexprlist = comma expr;
	
	expr = {operation} expr cond operand |
		{operand} operand;
		
	operand = {operation} operand addop term |
		{term} term;
	
	term = {operation} term multop factor |
		{factor} factor;
	
	factor = {paren} lparen expr rparen |
		{negate} minus factor |
		{int} int |
		{real} real |
		{boolean} booleanliteral |
		{id} id bracketedint? |
		{methodcall} id lparen varlisttwo rparen |
		{methodcallonsomething} [firstid]:id bracketedint? period [secondid]:id
			lparen varlisttwo rparen;
			
	boolean = {literal} booleanliteral |
		{cond} expr cond operand |
		{id} id;
	
	booleanliteral = {true} true |
		{false} false;
	
	cond = {equals} equals |
		{notequals} notequals |
		{greaterorequals} greaterorequals |
		{lessorequals} lessorequals |
		{greaterthan} greaterthan |
		{lessthan} lessthan;
	
	addop = {plus} plus |
		{minus} minus;
	
	multop = {times} times |
		{divide} divide;
	
	type = {int} inttype |
		{real} realtype |
		{string} stringtype |
		{boolean} booleantype |
		{void} voidtype |
		{id} id;
	
	additionalidlist = comma id;
	
	bracketedint = lbracket int rbracket;










