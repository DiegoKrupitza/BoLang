grammar Bo;

bo          :    stat+ ;

scopecont   :   stat* ;

stat        :   'return' expr ';'                                                                               #returnStat
            |   'if' '(' cond=expr ')' '{'  ifStat=scopecont '}' 'else' '{' elseStat=scopecont '}'              #ifElseStat
            |   'if' '(' cond=expr ')' '{'  ifStat=scopecont '}'                                                #ifStat
            |   'var' ID ':=' expr ';'                                                                          #varDefStat
            |   ID ':=' expr ';'                                                                                #varAssignStat
            |   expr ';'                                                                                        #exprForward
            ;

expr        :   '(' expr ')'                                                                                                       # parensExpr
            |   op=('-'|'!') expr                                                                                                  # unaryExpr
            |   left=expr op=('+' | '-' | '*' | '/' | '<' | '<=' | '>' | '>=' | '==' | '!=' | '&&' | '||' | '++') right=expr       # infixExpr
            |   term                                                                                                               # termExpr

            ;

term        :   num=NUM                                                     #numVal
            |   string                                                      #stringVal
            |   BOOLEAN                                                     #booleanEntry
            |   id=ID                                                       #identifierVal
            |   '#' id=ID                                                   #externalParamVal
            |   module=ID '.' func=ID '(' (expr ( ',' expr )*)? ')'         #funcVal
            |   '[' (expr ( ',' expr )*)? ']'                               #createList
            |   id=ID'[' index=expr ']'                                     #accessStringOrListItem
            ;



string      :   STRING
            ;


SINGLE_LINE_COMMENT :   '//' ~[\r\n]* -> skip;

WS          :   [ \t\r\n]+ -> skip ;

fragment DIGIT      :   [0-9];
fragment ESC:       '\\"' | '\\\\';

OP_ADD:         '+';
OP_SUB:         '-';
OP_MUL:         '*';
OP_DIV:         '/';
OP_LESS:        '<';
OP_LESSEQ:      '<=';
OP_GREATER:     '>';
OP_GREATEREQ:   '>=';
OP_EQUAL:       '==';
OP_NOTEQUAL:    '!=';
OP_AND:         '&&';
OP_OR:          '||';
OP_CONCAT:      '++';
OP_NEG:         '!';

NUM     :   (DIGIT+ | (DIGIT+'.'DIGIT+) | ('.'DIGIT+));
BOOLEAN :   TRUE | FALSE;
TRUE    :   'TRUE' | 'True' | 'true';
FALSE   :   'FALSE' | 'False' | 'false';
ID      :   [_A-Za-z] [_0-9A-Za-z]*;
STRING  :   '"' (~'"'|ESC)* '"';
