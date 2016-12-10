import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class homework {
	int bit = 0;
	ArrayList<HashMap<String,ArrayList<String>>> kb1;
	int counter = 0;
	public static void main(String args[]) throws Exception
	{
		homework c = new homework();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
		int q = Integer.parseInt(br.readLine()); //no of queries.
		String m[] = new String[q];
		int w = 0;
		while(q != 0)
		{
			//read queries;
			m[w++] = c.spaceDetect(br.readLine());
			q--;
		}
		int n = Integer.parseInt(br.readLine()); //no. of sentences
		ArrayList<String> kbfinal = new ArrayList<String>();
		while(n != 0)
		{
			String input = br.readLine();
			ArrayList<Object> as = new ArrayList<Object>();
		
			String t = c.spaceDetect(input);
		
			t = c.initParser(t);
			
			t = c.finalParser(t);

			as = c.ai_parser(t);

			as = c.parseImplication(as);

			as = c.parseNot(as);

			as = c.parseDistribution(as);

			as = c.CleanUp(as);
			String infix = c.toinfix(as);
			String kb[] = infix.split("&");
			for(int i = 0; i < kb.length; i++)
			{
				
				String st[] = kb[i].split(" ");
				for(int j = 0; j < st.length; j++)
				{
					System.out.println("Format:"+c.standardize(c.duplicates(c.filter(st[j]))));
					
					kbfinal.add(c.standardize(c.duplicates(c.filter(st[j]))));
				}
			}
			n--;
		}
		ArrayList<HashMap<String,ArrayList<String>>> kbf = c.buildKB(kbfinal);
		System.out.println(kbf);

		PrintWriter y = new PrintWriter(new File("output.txt"));
		for(int r = 0; r < m.length; r++)
		{
			String query = m[r];
			ArrayList<String> temp;
			temp = c.copyKbFinal(kbfinal);
			temp.add(c.filter(c.oppQuery(query)));
			System.out.println(c.buildKB(temp));
			boolean ans = c.canResolve(c.oppQuery(query),c.buildKB(temp));
			if(ans)
			{
				y.println("TRUE");
			}
			else
			{
				y.println("FALSE");
			}
		}
		y.close();
	
	}
	public String standardize(String input)
	{
		HashMap<String,String> hs = new HashMap<String,String>();
		String a[] = input.split("\\|");
		for(int i = 0; i < a.length; i++)
		{
			String t[] = getArgList(a[i]);
			for(int j = 0; j < t.length; j++)
			{
				if(!hs.containsKey(t[j]) && Character.isLowerCase(t[j].charAt(0)))
				{
					hs.put(t[j], "arg"+Integer.toString(counter++));
				}
			}
			
		}
		StringBuffer b = new StringBuffer();
		for(int i = 0; i < a.length; i++)
		{
			StringBuffer br = new StringBuffer();
			char as[] = a[i].toCharArray();
			if(as[0] == '~')
			{
				br.append(Character.toString(as[0]));
			}
			br.append(getKey(a[i]));
			br.append(Character.toString('('));
			String t[] = getArgList(a[i]);
			for(int j = 0; j < t.length; j++)
			{
				if(hs.containsKey(t[j]))
					br.append(hs.get(t[j])+",");
				else
					br.append(t[j]+",");
			}
			br.deleteCharAt(br.length() - 1);
			br.append(")");
			
			b.append(br.toString()+"|");
		}
	
		
		return b.substring(0,b.length() - 1);
	}
	public String duplicates(String input)
	{
		HashSet<String> hs = new HashSet<String>();
		String a[] = input.split("\\|");
		for(int i = 0; i < a.length; i++)
		{
			hs.add(a[i]);
		}
		StringBuilder br = new StringBuilder();
		Iterator<String> t = hs.iterator();
		while(t.hasNext())
		{
			br.append(t.next()+"|");
		}
		return br.substring(0, br.length());
	}
	public ArrayList<String> copyKbFinal(ArrayList<String> input)
	{
		return (ArrayList<String>)input.clone();
	}
	public void makeKbGlobal(ArrayList<HashMap<String,ArrayList<String>>> k)
	{
		kb1 = k;
	}
	public boolean canResolve(String query,ArrayList<HashMap<String,ArrayList<String>>> kb)
	{
		String key = getKey(query);
		for(int i = 0; i < kb.size(); i++)
		{
			HashMap<String,ArrayList<String>> hs = kb.get(i);
			int record = 0;
			if(hs.containsKey(key))
			{
				ArrayList<String> g = hs.get(key);
				boolean findNeg = findNegative(query);

				if(findNeg)  //find negative
				{
					for(int j = 0; j < g.size(); j++)
					{
						String t = g.get(j);
						if(t.charAt(0) == '~') //can check for unification
						{
							ArrayList<String> y = canUnify(query,t);
							if(y != null) //can unify
							{
								String r[] = getArgList(t);
								HashMap<String,String> binding = new HashMap<String,String>();
								for(int k = 0; k < r.length; k++)
								{
									binding.put(r[k], y.get(k));	
								}
								record = j;
								ArrayList<String> input = new ArrayList<String>();
								for(String k : hs.keySet())
								{
									if(!k.equals(key))
									{
										ArrayList<String> temp = hs.get(k);
										for(String ti : temp)
										{
											input.add(unify(binding,ti));
										}
									}
								}
								
								for(int p = 0; p < g.size(); p++)
								{
									if(p != record)
									{
										input.add(unify(binding,g.get(p)));
									}
								}
								if(input.size() == 0)
								{
									System.out.println("Resolved");
									return true;
								}
								int q = 0;
								ArrayList<HashMap<String,ArrayList<String>>> kbc = copyKb(kb);
								kbc.remove(i);
								for(q = 0; q < input.size(); q++)
								{
									//System.out.println("q "+q+"input_size "+input.size());
									boolean ans  = canResolve(input.get(q),kbc);
									//System.out.println(ans);
									if(!ans)
									{
									    System.out.println("Cannot Resolve!");
										break;
									}
									//System.out.println("q:"+q+"size:"+input.size()+"should work");
								}
								if(q == input.size())
									return true;
							}
							
						}
					}
					
				}
				else //find positive
				{
					for(int j = 0; j < g.size(); j++)
					{
						String t = g.get(j);
						if(t.charAt(0) != '~') //can check for unification
						{
							ArrayList<String> y = canUnify(query,t);
							if(y != null) //can unify
							{
								String r[] = getArgList(t);
								HashMap<String,String> binding = new HashMap<String,String>();
								for(int k = 0; k < r.length; k++)
								{
									binding.put(r[k], y.get(k));	
								}
								record = j;
								ArrayList<String> input = new ArrayList<String>();
								for(String k : hs.keySet())
								{
									if(!k.equals(key))
									{
										ArrayList<String> temp = hs.get(k);
										for(String ti : temp)
										{
											input.add(unify(binding,ti));
										}
									}
								}
								
								for(int p = 0; p < g.size(); p++)
								{
									if(p != record)
									{
										input.add(unify(binding,g.get(p)));
									}
								}
								if(input.size() == 0)
								{
									System.out.println("Resolved");
									return true;
								}
								int q = 0;
								ArrayList<HashMap<String,ArrayList<String>>> kbc = copyKb(kb);
								kbc.remove(i);
								for(q = 0; q < input.size(); q++)
								{
									boolean ans  = canResolve(input.get(q),kbc);
									if(!ans)
									{
										System.out.println("Cannot Resolve!");
										break;
									}
								}
								if(q == input.size())
									return true;
							}
							
						}
					}
				}
			}
		}
		return false;
	}
	public ArrayList<HashMap<String,ArrayList<String>>> copyKb(ArrayList<HashMap<String,ArrayList<String>>> kb)
	{
		ArrayList<HashMap<String,ArrayList<String>>> kbc = new ArrayList<HashMap<String,ArrayList<String>>>();
		for(int i = 0; i < kb.size(); i++)
		{
			kbc.add(kb.get(i));
		}
		return kbc;
	}
	public String unify(HashMap<String,String> binding, String input)
	{
		StringBuilder br = new StringBuilder(getKey(input));
		if(input.charAt(0) == '~')
			br.insert(0, "~");
		br.append("(");
		String s[] = getArgList(input);
		int i = 0;
		for(i = 0; i < s.length; i++)
		{
			if(binding.containsKey(s[i]))
			{
				br.append(binding.get(s[i])+",");
			}
			else
			{
				br.append(s[i]+",");
			}
		}
		br.deleteCharAt(br.length()-1);
		br.append(")");
		return br.toString();
	}
	public boolean findNegative(String query)
	{
		if(query.charAt(0) == '~')
			return false;
		return true;
	}
	public ArrayList<String> canUnify(String s1, String s2)
	{
		ArrayList<String> s = new ArrayList<String>();
		String args1[] = getArgList(s1);
		String args2[] = getArgList(s2);
		HashMap<String,String> bound = new HashMap<String,String>();
		int i = 0;
		if(args1.length == args2.length)
		{
			for(i = 0; i < args1.length; i++)
			{
				if(isConstant(args1[i]) && isConstant(args2[i])) //arg1 is a constant and arg2 is a constant
				{
					if(!args1[i].equals(args2[i]))
					{
						break;
					}
					else
					{
						s.add(args1[i]);
					}
				}
				else if(isConstant(args1[i]) && !bound.containsKey(args2[i])) //arg1 is constant but arg2 is unbounded variable
				{
					s.add(args1[i]);
					bound.put(args2[i], args1[i]);
				}
				else if(bound.containsKey(args2[i])) //check for same variable same value
				{
					String b = bound.get(args2[i]);
					if(!b.equals(args1[i]))
					{
						break;
					}
					else
					{
						s.add(args1[i]);
					}
				}
				else  //arg1 is a variable
				{
					if(isVariable(args2[i]) && !bound.containsKey(args2[i]))
					{
						s.add(args1[i]);
						bound.put(args2[i],args1[i]);  //args2 is bounded.
					}
					else if(bound.containsKey(args2[i])) //check for same variable same value.
					{
						String b = bound.get(args2[i]);
						if(!b.equals(args1[i]))
						{
							break;
						}
						else
						{
							s.add(args1[i]);
						}
					}
					else //arg2 is a constant
					{
						s.add(args1[i]);
					}
				}
			}
			if(i < args1.length)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
		return s;
	}
	public boolean isVariable(String s)
	{
		if(Character.isLowerCase(s.charAt(0)))
			return true;
		else
			return false;
	}
	public boolean isConstant(String s)
	{
		if(Character.isUpperCase(s.charAt(0)))
			return true;
		return false;
	}
	public String[] getArgList(String s)
	{
		char a[] = s.toCharArray();
		int i = 0;
		while(i < a.length)
		{
			if(a[i] == '(')
			{
				break;
			}
			i++;
		}
		String s1[] = s.substring(i+1, s.length()-1).split(",");
		return s1;
	}
	public String oppQuery(String s)
	{
		StringBuilder br = new StringBuilder();
		char a[] = s.toCharArray();
		if(a[0] == '~')
		{
			br.append(s.substring(1, s.length()));
		}
		else
		{
			br.append(s);
			br.insert(0, "~");
		}
		return br.toString();
	}
	public ArrayList<HashMap<String,ArrayList<String>>> buildKB(ArrayList<String> input)
	{
		ArrayList<HashMap<String,ArrayList<String>>> kb = new ArrayList<HashMap<String,ArrayList<String>>>();
		
		for(int i = 0; i < input.size(); i++)
		{
			HashMap<String,ArrayList<String>> hs = new HashMap<String,ArrayList<String>>();
			//first disjunction
			String s[] = input.get(i).split("\\|");
			for(int j = 0; j < s.length; j++)
			{
				String key = getKey(s[j]);
				//System.out.println("Key is:"+key);
				if(hs.containsKey(key))
				{
					ArrayList<String> t = hs.get(key);
					t.add(s[j]);
					hs.put(key, t);
				}
				else
				{
					ArrayList<String> t = new ArrayList<String>();
					t.add(s[j]);
					hs.put(key, t);
				}
			}
			//System.out.println("Added:"+hs.values());
			kb.add(hs);
		}
		return kb;
	}
	public String getKey(String s)
	{
		StringBuilder br = new StringBuilder();
		//System.out.println("input"+s);
		char a[] = s.toCharArray();
		int i = 0;
		while(i < a.length)
		{
			if(Character.isAlphabetic(a[i]))
			{
				int j = i;
				while(j < a.length && Character.isAlphabetic(a[j]))
				{
					j++;
				}
				//System.out.println("i:"+i+"j:"+j);
				br.append(s.substring(i, j));
				//System.out.println(br);
				break;
			}
			i++;
		}
		return br.toString();
	}
	public String finalParser(String s)
	{
		StringBuilder sb = new StringBuilder();
		Stack<String> st = new Stack<String>();
		int i = s.length() - 1;
		char a[] = s.toCharArray();
		int flag = 1;
		while(i >= 0)
		{
			
			if(a[i] == ')') //operand
			{
				int j = i;
				while(j >= 0 && !isOperator(a[j]) && !isImplication(a[j]) && !(a[j] == '{') && !(a[j] == '}'))
				{
					j--;
				}
				
				sb.insert(0,"'"+s.substring(j+1, i+1)+"',");
				i = j;
			}
			else if(a[i] == '}') 
			{
				flag = 0;
				st.push(Character.toString(a[i]));
				i--;
			}
			else if(a[i] == '{')
			{
				flag = 0;
				//pop all operators.
				while(true)
				{
					if(!st.isEmpty())
					{
						String r = st.pop();
						if(r.equals("}"))
							break;
						else
						{
							sb = add(r,sb);
						}
					}
					else
					{
						break;
					}
				}
				i--;
			}
			else if(isOperator(a[i]))
			{
				flag = 0;
				//check top and a[i] precedence. either pop or push.
				String op = Character.toString(a[i]);
				if(!st.isEmpty())
				{
					String top = st.peek();
					while(!st.isEmpty() && precedenceLevel(st.peek()) >= precedenceLevel(op))
					{
						top = st.pop();
						sb = add(top,sb);
					}
					st.push(op);
				}
				else
				{
					st.push(op);
				}	
				i--;
			}
			else if(isImplication(a[i]))
			{
				flag = 0;
				String op =  Character.toString(a[i-1]) + Character.toString(a[i]);
				if(!st.isEmpty())
				{
					String top = st.peek();
					while(!st.isEmpty() && precedenceLevel(st.peek()) >= precedenceLevel(op))
					{
						top = st.pop();
						sb = add(top,sb);
					}
					st.push(op);
					
				}
				else
				{
					st.push(op);
				}
				i -= 2;
			}
			else
			{
				i--;
			}
		}
		while(!st.isEmpty())
		{
			String t = st.pop();
			sb = add(t,sb);
		}
		if(flag == 1)
		{
			sb.insert(0, "[");
			sb.replace(sb.length()-1,sb.length(),"]");
		}
		//empty stack;
		return sb.toString();
	}
	public StringBuilder add(String operator, StringBuilder sb)
	{
		if(operator.equals("~")) //one term
		{
			int i = firsttermEnd(sb,0);
			sb.replace(i,i+1,"],");
			sb.insert(0, "['"+operator+"',");
			
		}
		else //two terms
		{
			int i = firsttermEnd(sb,firsttermEnd(sb,0));
			sb.replace(i,i+1,"],");
			sb.insert(0, "['"+operator+"',");
		}
		return sb;
	}
	public int firsttermEnd(StringBuilder sb,int start)
	{
		int i = start;
		char a[]  = sb.toString().toCharArray();
		while(i < a.length)
		{
			if(a[i] == '[') //return last bracket index
			{
				Stack<Character> st = new Stack<Character>();
				st.push(a[i]);
				int j = i+1;
				while(!st.isEmpty())
				{
					if(a[j] == ']')
					{
						st.pop();
					}
					if(a[j] == '[')
						st.push(a[j]);
					j++;
						
				}
				return j;
				
			}
			else if(a[i] == '\'')//return apostrophe index
			{
				int j = i+1;
				while(j < a.length && a[j] != '\'')
					j++;
				return j+1;
			}
			i++;
		}
		return 1;
	}
	public int precedenceLevel(String s)
	{
		if(s.equals("}"))
		{
			return 0;
		}
		if(s.equals("~"))
		{
			return 4;
		}
		else if(s.equals("&"))
		{
			return 3;
		}
		else if(s.equals("|"))
		{
			return 2;
		}
		else if(s.equals("=>"))
		{
			return 1;
		}
		else 
			return 0;
	}
	public String spaceDetect(String s)
	{
		char a[] = s.toCharArray();
		int i = 0; 
		StringBuilder br = new StringBuilder();
		while(i < a.length)
		{
			if(a[i] == ' ')
			{
				int j = i+1;
				while(j < a.length && a[j] == ' ')
				{
					j++;
				}
				i = j;
			}
			else
			{
				br.append(Character.toString(a[i]));
				i++;
			}
		}
		return br.toString();
	}
	public String initParser(String s)
	{
		char a[] = s.toCharArray();
		int i = 0;  
		StringBuilder sb = new StringBuilder();
		while(i < a.length)
		{
			if(isOperator(a[i]))
			{
				sb.append(Character.toString(a[i]));
				i++;
			}	
			else if(isImplication(a[i]))
			{
				sb.append(s.subSequence(i, i+2));
				i = i+2;
			}
			else if(a[i] == '(' && (i-1) >= 0 && !Character.isAlphabetic(a[i-1]))
			{
				sb.append(Character.toString('{'));
				i++;
			}
			else if(a[i] == '(' && (i-1) <= 0)
			{
				sb.append(Character.toString('{'));
				i++;
			}
			else if(a[i] == ')' && (i-1) >= 0 && !Character.isAlphabetic(a[i-1]))
			{
				sb.append(Character.toString('}'));
				i++;
			}
			else if(Character.isAlphabetic(a[i]))
			{
				int j = i;
				while(j < a.length-1 && a[j] != ')')  //check once
					j++;
				sb.append(s.substring(i, j+1));
				i = j+1;
				
			}
			else
			{
				i++;
			}
		}
		return sb.toString();
	}
	public boolean isOperator(char a)
	{
		if(a == '&' || a == '|' || a == '~')
		{
			return true;
		}
		return false;
	}
	public boolean isImplication(char a)
	{
		if(a == '=' || a == '>')
		{
			return true;
		}
		return false;
	}
	public String filter(String t)
	{
		char a[] = t.toCharArray();
		char f[] = new char[a.length];
		Arrays.fill(f, ' ');
		int j = 0;
		for(int i = 0; i < a.length; i++)
		{
			if(a[i] != '[' && a[i] != ']')
				f[j++] = a[i];	
		}
		return new String(f).trim();
	}
	public String toinfix(ArrayList<Object> logic)
	{
		StringBuilder br = new StringBuilder();
		if(!logic.get(0).equals("~"))
		{
			if(logic.size() > 1)
			{
				for(int i = 1; i < logic.size(); i++)
				{
					if(logic.size() > 1 && logic.get(i).getClass() != new String().getClass())
					{
						br.append("["+toinfix((ArrayList<Object>)logic.get(i))+"]");
						
					}
					else
					{
						br.append(logic.get(i));
					}
					br.append(logic.get(0));
				}
				
			}
			else
			{
				br.append(logic.get(0));
				return br.toString();
			}
		}
		else if(logic.get(0).equals("~"))
		{
			br.append(logic.get(0));
			br.append(logic.get(1));
			br.append(logic.get(0));
		}
		else
		{
			br.append(logic.get(0));
			return br.toString();
		}
		return br.deleteCharAt(br.length()-1).toString();
	}
	public ArrayList<Object> cleanUp(ArrayList<Object> logic)
	{
		ArrayList<Object> result = new ArrayList<Object>();
		
		if(logic.get(0).equals("~"))
			return logic;
		
		result.add(logic.get(0));
		String outer_op = (String)logic.get(0);
		
		for(int i = 1; i < logic.size(); i++)
		{
			if(logic.get(i).getClass() != new String().getClass())
			{
				ArrayList<Object> t = (ArrayList<Object>)logic.get(i);
				if(t.get(0).equals(outer_op))
				{
					for(int j = 1; j < t.size(); j++)
					{
						result.add(t.get(j));
					}
				}
				else
				{
					result.add(t);
				}
			}
			else
			{
				result.add(logic.get(i));
			}
			
			
		}
		return result;
	}
	public ArrayList<Object> CleanUp(ArrayList<Object> logic)
	{
		logic = cleanUp(logic);

		for(int i = 1; i < logic.size(); i++)
		{
			if(logic.get(i).getClass() != new String().getClass())
			{
				ArrayList<Object> f = (ArrayList<Object>)logic.get(i);
				if(f.size() > 1)
				{
					logic.set(i,CleanUp((ArrayList<Object>)logic.get(i)));
				}
				
			}
		}
		logic = cleanUp(logic);
		return logic;
	}
	public ArrayList<Object> ai_parser(String s)
	{
		ArrayList<Object> as = new ArrayList<Object>();
		char a[] = s.toCharArray();
		int i = 1;
		while(i <= a.length-2)
		{
			if(a[i] == '\'')
			{
				int j = i+1;
				while(j < a.length && a[j] != '\'')
					j++;
				as.add(s.substring(i+1, j));
				i = j+1;

			}
			else if(a[i] == '[')
			{
				int j = i+1;
				Stack<Character> st = new Stack<Character>();
				st.push(a[i]);
				while(!st.isEmpty())
				{
					if(a[j] == '[')
						st.push(a[j]);
					else if(a[j] == ']')
						st.pop();
					j++;
				}
				as.add(ai_parser(s.substring(i, j)));
				i = j;
			}
			i++;
			
		}
		return as;
	}
	public ArrayList<Object> parseDistribution(ArrayList<Object> logic)
	{
		if(isDistributionCandidate(logic))
		{
			logic = distributeOr(logic);
		}
		for(int i = 1; i < logic.size(); i++)
		{
			if(logic.get(i).getClass() != (new String().getClass()))
			{
				ArrayList<Object> f = (ArrayList<Object>)logic.get(i);
				if(f.size() > 1)
				{
					logic.set(i,parseDistribution((ArrayList<Object>)logic.get(i)));
				}
				
			}	
				
		}
		if(isDistributionCandidate(logic))
		{
			logic = distributeOr(logic);
		}
		return logic;
	}
	public ArrayList<Object> distributeOr(ArrayList<Object> logic)
	{
		ArrayList<Object> result = new ArrayList<Object>();
		result.add("&");
		ArrayList<Object> list1 = new ArrayList<Object>();
		if(logic.get(1).getClass() != new String().getClass())
		{
			list1 = (ArrayList<Object>)logic.get(1);
		}
		
		ArrayList<Object> list2 = new ArrayList<Object>();
		if(logic.get(2).getClass() != new String().getClass())
			list2 = (ArrayList<Object>)logic.get(2);
		
		if(!list1.isEmpty() && !list2.isEmpty() && list1.get(0).equals("and") && list2.get(0).equals("and"))
		{
			ArrayList<Object> t1 = new ArrayList<Object>();
			t1.add("|");
			t1.add(list1.get(1));
			t1.add(list2.get(1));
			result.add(parseDistribution(t1));
			ArrayList<Object> t2 = new ArrayList<Object>();
			t2.add("|");
			t2.add(list1.get(1));
			t2.add(list2.get(2));
			result.add(parseDistribution(t2));
			ArrayList<Object> t3 = new ArrayList<Object>();
			t3.add("|");
			t3.add(list1.get(2));
			t3.add(list2.get(1));
			result.add(parseDistribution(t3));
			ArrayList<Object> t4 = new ArrayList<Object>();
			t4.add("|");
			t4.add(list1.get(2));
			t4.add(list2.get(2));
			result.add(parseDistribution(t4));
			
		}
		else
		{
			if(!list1.isEmpty() && list1.get(0).equals("&"))
			{
				if(!list2.isEmpty() && list2.size() > 2)
				{
					if(isDistributionCandidate(list2))
					{
						list2 = parseDistribution(list2);
						ArrayList<Object> t1 = new ArrayList<Object>();
						t1.add("|");
						t1.add(list1.get(1));
						t1.add(list2.get(1));
						result.add(parseDistribution(t1));
						ArrayList<Object> t2 = new ArrayList<Object>();
						t2.add("|");
						t2.add(list1.get(1));
						t2.add(list2.get(2));
						result.add(parseDistribution(t2));
						ArrayList<Object> t3 = new ArrayList<Object>();
						t3.add("|");
						t3.add(list1.get(2));
						t3.add(list2.get(1));
						result.add(parseDistribution(t3));
						ArrayList<Object> t4 = new ArrayList<Object>();
						t4.add("|");
						t4.add(list1.get(2));
						t4.add(list2.get(2));
						result.add(parseDistribution(t4));
						
					}
					else
					{
						ArrayList<Object> f1 = new ArrayList<Object>();
						f1.add("|");
						f1.add(list1.get(1));
						f1.add(logic.get(2));
						result.add(parseDistribution(f1));
						ArrayList<Object> f2 = new ArrayList<Object>();
						f2.add("|");
						f2.add(list1.get(2));
						f2.add(logic.get(2));
						result.add(parseDistribution(f2));
					}
				}
				else
				{
					ArrayList<Object> f1 = new ArrayList<Object>();
					f1.add("|");
					f1.add(list1.get(1));
					f1.add(logic.get(2));
					result.add(parseDistribution(f1));
					ArrayList<Object> f2 = new ArrayList<Object>();
					f2.add("|");
					f2.add(list1.get(2));
					f2.add(logic.get(2));
					result.add(parseDistribution(f2));
				}
			}
			else
			{
				if(!list1.isEmpty() && list1.size() > 2)
				{
					if(isDistributionCandidate(list1))
					{
						list1 = parseDistribution(list1);
						ArrayList<Object> t1 = new ArrayList<Object>();
						t1.add("|");
						t1.add(list1.get(1));
						t1.add(list2.get(1));
						result.add(parseDistribution(t1));
						ArrayList<Object> t2 = new ArrayList<Object>();
						t2.add("|");
						t2.add(list1.get(1));
						t2.add(list2.get(2));
						result.add(parseDistribution(t2));
						ArrayList<Object> t3 = new ArrayList<Object>();
						t3.add("|");
						t3.add(list1.get(2));
						t3.add(list2.get(1));
						result.add(parseDistribution(t3));
						ArrayList<Object> t4 = new ArrayList<Object>();
						t4.add("|");
						t4.add(list1.get(2));
						t4.add(list2.get(2));
						result.add(parseDistribution(t4));
					}
					else
					{
						ArrayList<Object> m1 = new ArrayList<Object>();
						m1.add("|");
						m1.add(logic.get(1));
						m1.add(list2.get(1));
						result.add(parseDistribution(m1));
						ArrayList<Object> m2 = new ArrayList<Object>();
						m2.add("|");
						m2.add(logic.get(1));
						m2.add(list2.get(2));
						result.add(parseDistribution(m2));
					}
				}
				else
				{
					ArrayList<Object> m1 = new ArrayList<Object>();
					m1.add("|");
					m1.add(logic.get(1));
					m1.add(list2.get(1));
					result.add(parseDistribution(m1));
					ArrayList<Object> m2 = new ArrayList<Object>();
					m2.add("|");
					m2.add(logic.get(1));
					m2.add(list2.get(2));
					result.add(parseDistribution(m2));
				}
			}
		}
		return result;
	}
	public ArrayList<Object> parseLogic(ArrayList<Object> logic)
	{
		if(logic.size() == 0)
			return logic;
		if(logic.size() == 1)
			return logic;
			
		return logic;
	}
	public ArrayList<Object> parseImplication(ArrayList<Object> logic)
	{
		if(isImpl(logic))
		{
			logic = eliminateImplication(logic);
		}
		for(int i = 1; i < logic.size(); i++)
		{
			if(logic.get(i).getClass() != (new String().getClass()))
			{
				ArrayList<Object> f = (ArrayList<Object>)logic.get(i);
				if(f.size() > 1)
				{
					logic.set(i,parseImplication((ArrayList<Object>)logic.get(i)));
				}
				
			}		
		}
		if(isImpl(logic))
		{
			logic = eliminateImplication(logic);
		}
		return logic;
	}
	public ArrayList<Object> eliminateImplication(ArrayList<Object> logic)
	{
		ArrayList<Object> result = new ArrayList<Object>();
		result.add("|");
		ArrayList<Object> mid = new ArrayList<Object>();
		mid.add("~");
		mid.add(logic.get(1));
		result.add(mid);
		result.add(logic.get(2));
		return result;
	}
	public ArrayList<Object> propogateNot(ArrayList<Object> logic)
	{
		ArrayList<Object> result = new ArrayList<Object>();
		
		ArrayList<Object> f = (ArrayList<Object>)logic.get(1);
		
		if(f.get(0).equals("|"))
			result.add("&");
		else if(f.get(0).equals("&"))
		{
			result.add("|");
		}
		else if(f.get(0).equals("~"))
		{
			if(f.get(1).getClass() != new String().getClass())
				return (ArrayList<Object>)f.get(1);
			else 
			{
				String s = (String)f.get(1);
				ArrayList<Object> t = new ArrayList<Object>();
				t.add(s);
				System.out.println("In: "+t);
				bit = 1;
				return t;
			}
		}
		else if(f.get(0).getClass() == new String().getClass())
		{
			result.add("~");
			result.add(f.get(0));
		}
		for(int i = 1; i < f.size(); i++)
		{
			if(f.get(i).getClass() != new String().getClass())
			{
				ArrayList<Object> t = new ArrayList<Object>();
				t.add("~");
				t.add(f.get(i)); 
				ArrayList<Object> s = propogateNot(t);
				if(bit == 0)
					result.add(s);
				else
				{
					result.add(s.get(0));
				}
					
			}
			else
			{
				ArrayList<Object> t = new ArrayList<Object>();
				t.add("~");
				t.add(f.get(i));
				result.add(t);
			}
		}
		return result;
	}
	public ArrayList<Object> parseNot(ArrayList<Object> logic)
	{
		if(isNPCandidate(logic))
		{
			logic = propogateNot(logic);
		}
		
		for(int i = 1; i < logic.size(); i++)
		{
			if(logic.get(i).getClass() != (new String().getClass()))
			{
				ArrayList<Object> f = (ArrayList<Object>)logic.get(i);
				if(f.size() > 1)
				{
					logic.set(i,parseNot((ArrayList<Object>)logic.get(i)));
				}
				
			}
		}
		if(isNPCandidate(logic))
		{
			logic = propogateNot(logic);
		}
		return logic;
	}
	public boolean isImpl(ArrayList<Object> logic)
	{
		if(logic.get(0).getClass() == (new String().getClass()) && logic.get(0).equals("=>") && logic.size() ==3)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	public boolean isNPCandidate(ArrayList<Object> logic)
	{
		
		if(logic.get(0).getClass() == (new String().getClass()) && logic.get(0).equals("~") && logic.size() == 2 && logic.get(1).getClass() != (new String().getClass()))
		{
			return true;
		}
		return false;
	}
	public boolean isDistributionCandidate(ArrayList<Object> logic)
	{
		if(logic.get(0).getClass() == (new String().getClass()) && logic.get(0).equals("|"))
		{
			for(int i = 1; i < logic.size(); i++)
			{
				if(logic.get(i).getClass() != (new String().getClass()))
				{
					ArrayList<Object> f = (ArrayList<Object>)logic.get(i);
					if(f.size() > 1)
					{
						if(f.get(0).getClass() == new String().getClass() && f.get(0).equals("&"))
						{
							return true;
						}
					}
					
				}
			}
		}
		return false;
	}
}
