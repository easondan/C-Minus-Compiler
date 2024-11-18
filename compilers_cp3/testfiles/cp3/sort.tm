* C-Minus Compilation to TM Code
* File: 
* Standard prelude
  0:    LD 6, 0(0)	load gp with maxaddr
  1:   LDA 5, 0(6)	copy gp to fp
  2:    ST 0, 0(0)	clear value at location 0
* Jump around i/o routines here
* Code for input routine
  4:    ST 0, -1(5)	store return
  5:    IN 0, 0, 0	input
  6:    LD 7, -1(5)	return to caller
* Code for output routine
  7:    ST 0, -1(5)	store return
  8:    LD 0, -2(5)	load output value
  9:   OUT 0, 0, 0	output
 10:    LD 7, -1(5)	return to caller
  3:   LDA 7, 7(7)	jump around i/o code
* End of standard prelude.
* processing global array: x
* processing function: minloc
* jump around function body here
 11:    ST 0, -1(5)	store return
* processing local array: a
* processing local var: low
* processing local var: high
* -> compound statement
* processing local var: i
* processing local var: x
* processing local var: k
* -> op 
* -> id
* looking up id:  k
 13:   LDA 0, 0(5)	load id address
* <- id
 14:    ST 0, -5(5)	op: push left
* -> id
* looking up id:  low
 15:    LD 0, 0(5)	load id value
* <- id
 16:    ST 0, -5(5)	op: push left
 17:    LD 0, -5(5)	op: load left
 18:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> id
* looking up id:  x
 19:   LDA 0, 0(5)	load id address
* <- id
 20:    ST 0, -5(5)	op: push left
* -> subs
* -> id
* looking up id:  low
 21:    LD 0, 0(5)	load id value
* <- id
 22:    ST 0, -5(5)	op: push left
* <- subs
 23:    LD 0, -5(5)	op: load left
 24:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> id
* looking up id:  i
 25:   LDA 0, 0(5)	load id address
* <- id
 26:    ST 0, -5(5)	op: push left
* -> op
* -> id
* looking up id:  low
 27:    LD 0, 0(5)	load id value
* <- id
 28:    ST 0, -5(5)	op: push left
* -> constant
 29:   LDC 0, 1(0)	load const
* <- constant
 30:    LD 1, -5, 5	op: load left
 31:  PLUS 0, 1, 0	op +
* <- op
 32:    LD 0, -5(5)	op: load left
 33:    ST 0, 0(1)	assign: store value
* <- op
* -> while
* while: jump after body comes back here
* -> op
* -> id
* looking up id:  i
 35:    LD 0, 0(5)	load id value
* <- id
 36:    ST 0, -5(5)	op: push left
* -> id
* looking up id:  high
 37:    LD 0, 0(5)	load id value
* <- id
 38:    ST 0, -5(5)	op: push left
 39:    LD 1, -5, 5	op: load left
 40:   SUB 0, 1, 0	op <
 41:   JGE 0, 2(7)	br if true
 42:   LDC 0, 0, 0	false case
 43:   LDA 0, 1, 0	unconditional jmp
 44:   LDC 0, 1, 0	true case
* <- op
* while: jump to end belongs here
* -> compound statement
* -> if
* -> op
* -> subs
* -> id
* looking up id:  i
 45:    LD 0, 0(5)	load id value
* <- id
 46:    ST 0, -2(5)	op: push left
* <- subs
* -> id
* looking up id:  x
 47:    LD 0, 0(5)	load id value
* <- id
 48:    ST 0, -2(5)	op: push left
 49:    LD 1, -2, 5	op: load left
 50:   SUB 0, 1, 0	op <
 51:   JGE 0, 2(7)	br if true
 52:   LDC 0, 0, 0	false case
 53:   LDA 0, 1, 0	unconditional jmp
 54:   LDC 0, 1, 0	true case
* <- op
* if: jump to else belongs here
* -> compound statement
* -> op 
* -> id
* looking up id:  x
 55:   LDA 0, 0(5)	load id address
* <- id
 56:    ST 0, -2(5)	op: push left
* -> subs
* -> id
* looking up id:  i
 57:    LD 0, 0(5)	load id value
* <- id
 58:    ST 0, -2(5)	op: push left
* <- subs
 59:    LD 0, -2(5)	op: load left
 60:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> id
* looking up id:  k
 61:   LDA 0, 0(5)	load id address
* <- id
 62:    ST 0, -2(5)	op: push left
* -> id
* looking up id:  i
 63:    LD 0, 0(5)	load id value
* <- id
 64:    ST 0, -2(5)	op: push left
 65:    LD 0, -2(5)	op: load left
 66:    ST 0, 0(1)	assign: store value
* <- op
* if: jump to end belongs here
 67:   JEQ 0, 0(7)	if: jmp to else
* -> op 
* -> id
* looking up id:  i
 68:   LDA 0, 0(5)	load id address
* <- id
 69:    ST 0, -2(5)	op: push left
* -> op
* -> id
* looking up id:  i
 70:    LD 0, 0(5)	load id value
* <- id
 71:    ST 0, -2(5)	op: push left
* -> constant
 72:   LDC 0, 1(0)	load const
* <- constant
 73:    LD 1, -2, 5	op: load left
 74:  PLUS 0, 1, 0	op +
* <- op
 75:    LD 0, -2(5)	op: load left
 76:    ST 0, 0(1)	assign: store value
* <- op
 34:   LDA 7, 42(7)	
* <- while
* -> return
* -> id
* looking up id:  k
 77:    LD 0, 0(5)	load id value
* <- id
 78:    ST 0, -2(5)	op: push left
 79:    LD 7, -1(5)	return to caller
* <- return
* end of function: minloc
 80:    LD 7, -1(5)	return to caller
* processing function: sort
* jump around function body here
 81:    ST 0, -1(5)	store return
* processing local array: a
* processing local var: low
* processing local var: high
* -> compound statement
* processing local var: i
* processing local var: k
* -> op 
* -> id
* looking up id:  i
 83:   LDA 0, 1(5)	load id address hello
* <- id
 84:    ST 0, -4(5)	op: push left
* -> id
* looking up id:  low
 85:    LD 0, 1(5)	load id value
* <- id
 86:    ST 0, -4(5)	op: push left
 87:    LD 0, -4(5)	op: load left
 88:    ST 0, 0(1)	assign: store value
* <- op
* -> while
* while: jump after body comes back here
* -> op
* -> id
* looking up id:  i
 90:    LD 0, 1(5)	load id value
* <- id
 91:    ST 0, -4(5)	op: push left
* -> op
* -> id
* looking up id:  high
 92:    LD 0, 1(5)	load id value
* <- id
 93:    ST 0, -4(5)	op: push left
* -> constant
 94:   LDC 0, 1(0)	load const
* <- constant
 95:    LD 1, -4, 5	op: load left
 96:   SUB 0, 1, 0	op -
* <- op
 97:    LD 1, -4, 5	op: load left
 98:   SUB 0, 1, 0	op <
 99:   JGE 0, 2(7)	br if true
100:   LDC 0, 0, 0	false case
101:   LDA 0, 1, 0	unconditional jmp
102:   LDC 0, 1, 0	true case
* <- op
* while: jump to end belongs here
* -> compound statement
* processing local var: t
* -> op 
* -> id
* looking up id:  k
103:   LDA 0, 1(5)	load id address hello
* <- id
104:    ST 0, -3(5)	op: push left
* -> call of function: minloc
* -> id
* looking up id:  a
105:    LD 0, 0(5)	load id value
* <- id
106:    ST 0, -3(5)	op: push left
* -> id
* looking up id:  i
107:    LD 0, 1(5)	load id value
* <- id
108:    ST 0, -3(5)	op: push left
* -> id
* looking up id:  high
109:    LD 0, 1(5)	load id value
* <- id
110:    ST 0, -3(5)	op: push left
111:    ST 5, 0(5)	push ofp
112:   LDA 5, 0(5)	push frame
113:   LDA 0, 1(7)	load ac with ret ptr
114:   LDA 7, -108(7)	jump to fun loc
115:    LD 5, 0(5)	pop frame
* <- call
116:    LD 0, -3(5)	op: load left
117:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> id
* looking up id:  t
118:   LDA 0, 1(5)	load id address hello
* <- id
119:    ST 0, -3(5)	op: push left
* -> subs
* -> id
* looking up id:  k
120:    LD 0, 1(5)	load id value
* <- id
121:    ST 0, -3(5)	op: push left
* <- subs
122:    LD 0, -3(5)	op: load left
123:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> subs
* -> id
* looking up id:  k
124:   LDA 0, 1(5)	load id address hello
* <- id
125:    ST 0, -3(5)	op: push left
* <- subs
* -> subs
* -> id
* looking up id:  i
126:    LD 0, 1(5)	load id value
* <- id
127:    ST 0, -3(5)	op: push left
* <- subs
128:    LD 0, -3(5)	op: load left
129:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> subs
* -> id
* looking up id:  i
130:   LDA 0, 1(5)	load id address hello
* <- id
131:    ST 0, -3(5)	op: push left
* <- subs
* -> id
* looking up id:  t
132:    LD 0, 1(5)	load id value
* <- id
133:    ST 0, -3(5)	op: push left
134:    LD 0, -3(5)	op: load left
135:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> id
* looking up id:  i
136:   LDA 0, 1(5)	load id address hello
* <- id
137:    ST 0, -3(5)	op: push left
* -> op
* -> id
* looking up id:  i
138:    LD 0, 1(5)	load id value
* <- id
139:    ST 0, -3(5)	op: push left
* -> constant
140:   LDC 0, 1(0)	load const
* <- constant
141:    LD 1, -3, 5	op: load left
142:  PLUS 0, 1, 0	op +
* <- op
143:    LD 0, -3(5)	op: load left
144:    ST 0, 0(1)	assign: store value
* <- op
 89:   LDA 7, 55(7)	
* <- while
* end of function: sort
145:    LD 7, -1(5)	return to caller
* processing function: main
* jump around function body here
146:    ST 0, -1(5)	store return
* -> compound statement
* processing local var: i
* -> op 
* -> id
* looking up id:  i
148:   LDA 0, 1(5)	load id address hello
* <- id
149:    ST 0, -3(5)	op: push left
* -> constant
150:   LDC 0, 0(0)	load const
* <- constant
151:    LD 0, -3(5)	op: load left
152:    ST 0, 0(1)	assign: store value
* <- op
* -> while
* while: jump after body comes back here
* -> op
* -> id
* looking up id:  i
154:    LD 0, 1(5)	load id value
* <- id
155:    ST 0, -3(5)	op: push left
* -> constant
156:   LDC 0, 10(0)	load const
* <- constant
157:    LD 1, -3, 5	op: load left
158:   SUB 0, 1, 0	op <
159:   JGE 0, 2(7)	br if true
160:   LDC 0, 0, 0	false case
161:   LDA 0, 1, 0	unconditional jmp
162:   LDC 0, 1, 0	true case
* <- op
* while: jump to end belongs here
* -> compound statement
* -> op 
* -> subs
* -> id
* looking up id:  i
163:   LDA 0, 1(5)	load id address hello
* <- id
164:    ST 0, -2(5)	op: push left
* <- subs
* -> call of function: input
165:    ST 5, 0(5)	push ofp
166:   LDA 5, 0(5)	push frame
167:   LDA 0, 1(7)	load ac with ret ptr
168:   LDA 7, -162(7)	jump to fun loc
169:    LD 5, 0(5)	pop frame
* <- call
170:    LD 0, -2(5)	op: load left
171:    ST 0, 0(1)	assign: store value
* <- op
* -> op 
* -> id
* looking up id:  i
172:   LDA 0, 1(5)	load id address hello
* <- id
173:    ST 0, -2(5)	op: push left
* -> op
* -> id
* looking up id:  i
174:    LD 0, 1(5)	load id value
* <- id
175:    ST 0, -2(5)	op: push left
* -> constant
176:   LDC 0, 1(0)	load const
* <- constant
177:    LD 1, -2, 5	op: load left
178:  PLUS 0, 1, 0	op +
* <- op
179:    LD 0, -2(5)	op: load left
180:    ST 0, 0(1)	assign: store value
* <- op
153:   LDA 7, 27(7)	
* <- while
* -> call of function: sort
* -> id
* looking up id:  x
181:    LD 0, 0(5)	load id value
* <- id
182:    ST 0, -2(5)	op: push left
* -> constant
183:   LDC 0, 0(0)	load const
* <- constant
* -> constant
184:   LDC 0, 10(0)	load const
* <- constant
185:    ST 5, 0(5)	push ofp
186:   LDA 5, 0(5)	push frame
187:   LDA 0, 1(7)	load ac with ret ptr
188:   LDA 7, -182(7)	jump to fun loc
189:    LD 5, 0(5)	pop frame
* <- call
* -> op 
* -> id
* looking up id:  i
190:   LDA 0, 1(5)	load id address hello
* <- id
191:    ST 0, -2(5)	op: push left
* -> constant
192:   LDC 0, 0(0)	load const
* <- constant
193:    LD 0, -2(5)	op: load left
194:    ST 0, 0(1)	assign: store value
* <- op
* -> while
* while: jump after body comes back here
* -> op
* -> id
* looking up id:  i
196:    LD 0, 1(5)	load id value
* <- id
197:    ST 0, -2(5)	op: push left
* -> constant
198:   LDC 0, 10(0)	load const
* <- constant
199:    LD 1, -2, 5	op: load left
200:   SUB 0, 1, 0	op <
201:   JGE 0, 2(7)	br if true
202:   LDC 0, 0, 0	false case
203:   LDA 0, 1, 0	unconditional jmp
204:   LDC 0, 1, 0	true case
* <- op
* while: jump to end belongs here
* -> compound statement
* -> call of function: output
* -> subs
* -> id
* looking up id:  i
205:    LD 0, 0(5)	load id value
* <- id
206:    ST 0, -2(5)	op: push left
* <- subs
207:    ST 5, 0(5)	push ofp
208:   LDA 5, 0(5)	push frame
209:   LDA 0, 1(7)	load ac with ret ptr
210:   LDA 7, -204(7)	jump to fun loc
211:    LD 5, 0(5)	pop frame
* <- call
* -> op 
* -> id
* looking up id:  i
212:   LDA 0, 1(5)	load id address hello
* <- id
213:    ST 0, -2(5)	op: push left
* -> op
* -> id
* looking up id:  i
214:    LD 0, 1(5)	load id value
* <- id
215:    ST 0, -2(5)	op: push left
* -> constant
216:   LDC 0, 1(0)	load const
* <- constant
217:    LD 1, -2, 5	op: load left
218:  PLUS 0, 1, 0	op +
* <- op
219:    LD 0, -2(5)	op: load left
220:    ST 0, 0(1)	assign: store value
* <- op
195:   LDA 7, 25(7)	
* <- while
* end of function: main
221:    LD 7, -1(5)	return to caller
* Standard finale
222:    ST 5, 0(5)	push ofp
223:   LDA 5, 0(5)	push frame
224:   LDA 0, 1(7)	load ac with ret ptr
225:   LDA 7, -5(7)	jump to main loc
226:    LD 5, 0(5)	pop frame
* End of execution
227:  HALT 0, 0, 0	
