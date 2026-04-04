grammar Micro;

// Parser Rules
program : statement+ EOF ;

statement : varDecl ';'
          | assignStmt ';'
          | printStmt ';'
          | forLoop
          ;

varDecl : type ID '=' expr ;

assignStmt : ID '=' expr ;

printStmt : 'print' '(' expr ')' ;

forLoop : 'for' '(' varDecl ';' expr ';' ID '=' expr ')' '{' statement* '}' ;

type : 'int' | 'double' ;

expr : expr ('*' | '/' | '%') expr         # mulDivExpr
     | expr ('+' | '-') expr               # addSubExpr
     | expr ('<<' | '>>') expr             # shiftExpr
     | expr '&' expr                       # bitAndExpr
     | expr ('<' | '<=' | '>' | '>=') expr # relExpr
     | expr ('==' | '!=') expr             # eqExpr
     | ID                                  # idExpr
     | INT_LIT                             # intExpr
     | DOUBLE_LIT                          # doubleExpr
     | '(' expr ')'                        # parenExpr
     ;

// Lexer Rules
ID : [a-zA-Z_][a-zA-Z0-9_]* ;
INT_LIT : [0-9]+ ;
DOUBLE_LIT : [0-9]+ '.' [0-9]+ ;

WS : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;
