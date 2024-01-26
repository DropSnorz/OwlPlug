grammar ReaperProject;

root: . name (SPACE value)* element* CLOSE_NODE BOL* EOF;
node: OPEN_NODE name (SPACE value)* element* (SPACE)* CLOSE_NODE;
element: node | leaf;
name: prop;
value: prop;
prop: STRING | ANY+;
leaf: RAW_STRING | (BOL (SPACE | ANY | STRING )*);
STRING: '"' ~'"'* '"';
RAW_STRING: BOL SPACE* '|' (ANY | SPACE)*;
OPEN_NODE: BOL SPACE* '<';
CLOSE_NODE: BOL SPACE* '>';
SPACE: ' ';
BOL: '\n' ;
WS: [\r\t]+ -> skip;
ANY: ~[ \n];
