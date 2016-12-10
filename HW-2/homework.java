import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class homework {
	
	State root = null;
	int counter = 0;
	int curr_opp = 0;
	int max_depth = 0;
	int a[][];
	int count = 0;
	public static void main(String args[]) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/devangjhaveri/Documents/workspace/AI_Assignment/src/input.txt")));
		int n = Integer.parseInt(br.readLine());
		String mode = br.readLine();
		String play = br.readLine();
		int ply;
		PrintWriter w = new PrintWriter(new File("/Users/devangjhaveri/Documents/workspace/AI_Assignment/src/output.txt"));
		if(play.equals("X"))
			ply = 1;
		else 
			ply = 2;
		int depth = Integer.parseInt(br.readLine());
		int v[][] = new int[n][n];
		int state[][] = new int[n][n];
		for(int i = 0; i < n; i++)
		{
			String s[] = br.readLine().split(" ");
			for(int j = 0; j < n; j++)
			{
				v[i][j] = Integer.parseInt(s[j]);
			}
			
		}
		for(int i = 0; i < n; i++)
		{
			char t[] = br.readLine().toCharArray();
			for(int j = 0; j < n; j++)
			{
				if(t[j] == 'X')
					state[i][j] = 1;
				else if(t[j] == 'O')
					state[i][j] = 2;
				else
					state[i][j] = 0;
			}
			
		}
		homework t = new homework();
		t.setVal(v);
		t.setDepth(depth);
		t.setOpp(ply);
		State start = new State(state,-999999,999999,0,0,null,null,null,0,0,ply,t.getCounter(),"None");
		if(t.getRoot() == null)
			 t.setRoot(start);
		if(mode.equals("MINIMAX"))
		{
			//System.out.println("Minimax");
			t.BuildTree(t.getRoot());
		}
		else
		{
			//System.out.println("Alphabeta");
			t.BuildTreeA(t.getRoot());
		}
		//System.out.println("Out");
		if(t.getRoot().child != null)
		{
			int p = t.getRoot().child.move_i;
			int q = t.getRoot().child.move_j;
			char qi = (char)(q+65);
			int pi = p+1;
			w.println(""+qi+pi+" "+t.getRoot().child.type);
			int st[][] = t.getRoot().child.state;
			for(int i = 0; i < st.length; i++)
			{
				for(int j = 0; j < st.length; j++)
				{
					if(st[i][j] == 2)
						w.print("O");
					if(st[i][j] == 1)
						w.print("X");
					if(st[i][j] == 0)
						w.print(".");
				}
				w.println();
			}
			w.close();
		
		}
	}
		
	public void setRoot(State s)
	{
		root = s;
	}
	public void setVal(int v[][])
	{
		a = v;
	}
	public int getCounter()
	{
		return counter;
	}
	public void setDepth(int d)
	{
		max_depth = d;
	}
	public void setOpp(int ply)
	{
		curr_opp = ply;
	}
	public State getRoot()
	{
		return root;
	}
	public void BuildTreeA(State root)
	{
		if(root.depth != max_depth)
		{
			root.children = getChildren(root);
			if(root.children == null)
			{
				System.out.println("Gameover");
				getScoreA(root);
				return;
			}	
			
			Iterator<State> e = root.children.iterator();
			while (e.hasNext())
			{
				State s = e.next();
				
				if(root.gx < root.go)
				{
					s.gx = root.gx;
					s.go = root.go;
					BuildTreeA(s);
					if(root.ply != curr_opp)  //min node - change beta
					{
						if(root.go > s.gx)
						{
							root.go = s.gx;
							root.child = s;
						}
					}
					else // max node - change alpha
					{
						if(root.gx < s.go)
						{
							root.gx = s.go;
							root.child = s;
						}
					}
				}
				else
				{
					break; //no need to see further
					
				}
				
			}
			if(root.parent != null)
			{
				State x = root.parent;
				if(x.ply != curr_opp)  //min node - change beta
				{
					if(x.go > root.gx)
					{
						x.go = root.gx;
						x.child = root;
					}
				}
				else // max node - change alpha
				{
					if(x.gx < root.go)
					{
						x.gx = root.go;
						x.child = root;
					}
				}
			}
			if(root.child != null)
			{
				root.child = copyState(root.child);
				root.children.clear();
			}
			
		}
		else //calc eval values for leaves 
		{
			getScoreA(root);
		}
	}
	public void getScoreA(State current)
	{
		int xplus = 0;
		int xminus = 0;
		int n = a.length;
		int state[][] = current.state;
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				if(state[i][j] == curr_opp)
					xplus += a[i][j];
				else if(state[i][j] == opp(curr_opp))
					xminus += a[i][j];
			}
		}
		current.gx = xplus - xminus;
		current.go = xplus - xminus;
	}
	public int max(int a,int b)
	{
		if(a > b)
			return a;
		else
			return b;
	}
	public int min(int a,int b)
	{
		if(a > b)
			return b;
		else
			return a;
	}
	public void BuildTree(State root)
	{
		if(root.depth != max_depth)
		{
			root.children = getChildren(root);
			if(root.children == null)
			{
				getScore(root);
				return;
			}	
			Iterator<State> e = root.children.iterator();
			int flag = 0;
			while(e.hasNext())
			{
				State s = e.next();
				
				BuildTree(s);
		
				if(root.ply != curr_opp)
				{
						if(flag == 0)
						{
							flag = 1;
							root.sc = s.sc;
							root.child = s;
						}
						else
						{
							if(root.sc > s.sc)
							{
								root.sc = s.sc;
								root.child = s;
							}
						}
					
				}
				else
				{
					if(flag == 0)
					{
						flag = 1;
						root.sc = s.sc;
						root.child = s;
					}
					else
					{
						//System.out.println("Max:"+root.sc +" "+s.sc);
						if(root.sc < s.sc)
						{
							root.sc = s.sc;
							root.child = s; 
						}
					}
				}
				
			}
			root.child = copyState(root.child);
			root.children.clear();
			//System.gc();
		}
		else //calc eval values for leaves 
		{
			getScore(root);
			//System.out.println(root.sc);
		}
	}
	public State copyState(State current)
	{
		State temp = new State(current.state,current.go,current.gx,current.sc,current.depth,current.parent,current.children,current.child,current.move_i,current.move_j,current.ply,current.state_id,current.type);
		return temp;
	}
	public int[][] array_copy(int a[][],int n)
	{
		int copy[][] = new int[n][n];
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				copy[i][j] = a[i][j];
			}
		}
		return copy;
	}
	public int opp(int a)
	{
		if(a == 1)
			return 2;
		else
			return 1;
	}
	public ArrayList<State> getChildren(State start)
	{
		ArrayList<State> ch = new ArrayList<State>();
		//get Stake children;
		int n = a.length;
		int state[][] = start.state;
		int dep = start.depth;
		int state_copy[][];
		int cur_opp = start.ply;
		int flag = 0;
		int st[][];
		int alpha = start.gx;
		int beta = start.go;
		//System.out.println(cur_opp);
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				if(state[i][j] == 0)
				{
					flag = 1;
					state_copy = array_copy(state,n);
					state_copy[i][j] = cur_opp;
					//System.out.println("i:"+i+"j:"+j);
					ch.add(new State(state_copy,alpha,beta,0,dep+1,start,null,null,i,j,opp(start.ply),++counter,"Stake"));
				}
			}
		}
		if(flag == 0)
		{
			//no more positions left
			return null;
		}
		//get raid children
		st = start.state;
		int p = 0, q = 0;
		int move[] = new int[2];
		for(int i = 0; i < state.length; i++)
		{
			for(int j = 0; j < state.length; j++)
			{
				if(st[i][j] == 0)
				{
					if((j - 1 >= 0 && st[i][j-1] == cur_opp) || (i - 1 >= 0 && st[i-1][j] == cur_opp) || (j+1 < state.length && st[i][j+1] == cur_opp) || (i + 1 < state.length && st[i+1][j] == cur_opp)) // left
					{
						state_copy = array_copy(st,n);
						state_copy[i][j] = cur_opp;
						p = i;
						q = j;
						//check all four 
						//System.out.println(i+" "+j);
						if(q - 1 >= 0 && state_copy[p][q-1] == opp(cur_opp))
						{
							state_copy[p][q-1] = cur_opp;
						}
						if(p - 1 >= 0 && state_copy[p-1][q] == opp(cur_opp)) //up
						{
							//System.out.println("Raid O");
							state_copy[p-1][q] = cur_opp;
						}
						if(q + 1 < state.length && state_copy[p][q+1] == opp(cur_opp)) //right
						{
							state_copy[p][q+1] = cur_opp;
						}
						if(p + 1 < state.length && state_copy[p+1][q] == opp(cur_opp)) //down
						{
							state_copy[p+1][q] = cur_opp;
						}
						//System.out.println("Raid:"+i+" "+j);
						ch.add(new State(state_copy,alpha,beta,0,dep+1,start,null,null,i,j,opp(start.ply),++counter,"Raid"));
							
					}
					
				}
			}
		}
		return ch;
	}
	public void getScore(State current)
	{
		
		int xplus = 0;
		int xminus = 0;
		int n = a.length;
		int state[][] = current.state;
		//System.out.println(curr_opp);
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				if(state[i][j] == curr_opp)
				{
					xplus += a[i][j];
				}
					
				else if(state[i][j] == opp(curr_opp))
					xminus += a[i][j];
			}
		}
		
		current.sc = xplus - xminus;
		//System.out.println(current.sc);
	}

}
class State
{
	State(int st[][],int game_x,int game_o,int score,int d,State p, ArrayList<State> c,State ch,int mi,int mj, int py,int id,String ti)
	{
		state = st;
		gx = game_x;
		go = game_o;
		depth = d;
		parent = p;
		children = c;
		ply = py;
		state_id = id;
		move_i = mi;
		move_j = mj;
		child = ch;
		type = ti;
		int sc = score;
	}
	State parent;
	ArrayList<State> children;
	int state[][];
	int gx;
	int go;
	int depth;
	int ply;
	int state_id;
	State child;
	int move_i;
	int move_j;
	String type;
	int sc;
}

