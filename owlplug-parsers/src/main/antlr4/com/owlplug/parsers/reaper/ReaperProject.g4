grammar ReaperProject;


node: '<' NAME value* '\n' element* '>';
element: ( node | leaf ) '\n';
leaf: (NAME value+) | value;
value: STRING | INT | FLOAT | GUID | BOOL | BASE64 | LITERAL | NAME | VST_ID;
GUID: '{' HEX+ ('-' HEX+)* '}';
fragment HEX: [0-9a-fA-F];
STRING: '"' ~'"'* '"';
BOOL: '0' | '1';
INT: '-'? [0-9]+;
FLOAT: [0-9]+ '.' [0-9]+;
NAME: [_A-Z0-9]+;
LITERAL: [a-zA-Z0-9._-]+;
BASE64: [A-Za-z0-9+/=\n]+;
VST_ID: (INT '<' INT '>') | (INT '{' INT '}');
WS: [ \t\r]+ -> skip;
