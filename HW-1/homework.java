import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class homework {
	
	public static void main(String args[])throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
		String algo = br.readLine();
		//System.out.println(algo);
		String start = br.readLine();
		String goal = br.readLine();
		Hashtable<String,Integer> hs = new Hashtable<String,Integer>();
		Hashtable<Integer,String> hb = new Hashtable<Integer,String>();
		ArrayList<Node> ls = new ArrayList<Node>(10);
		int n = Integer.parseInt(br.readLine()); //no. of traffic lines;
		int c = 0;
		int d = 0;
		for(int i = 0; i < n; i++)
		{
			String s[] = br.readLine().split(" ");
			String a = s[0];
			String b = s[1];
			int cost = Integer.parseInt(s[2]);
			if(hs.get(a) == null) //add vertex to hashtable
			{
				hs.put(a, c++);
				hb.put(d++, a);
				ls.add(null);
			}
			if(hs.get(b) == null) //add vertex to hashtable
			{
				hs.put(b, c++);
				hb.put(d++, b);
				ls.add(null);
			}
			Node t = ls.get(hs.get(a));
			if(t == null)
			{
				 ls.set(hs.get(a),new Node(hs.get(b),cost));
			}
			else
			{
				while(t.next != null)
				{
					t = t.next;
				}
				t.next = new Node(hs.get(b),cost);
			}
			
			
		}
		int nodes = Integer.parseInt(br.readLine());
		int h[] = new int[nodes];
		for(int i = 0; i < nodes; i++)
		{
			String s[] = br.readLine().split(" ");
			try{
				h[hs.get(s[0])] = Integer.parseInt(s[1]);
			}
			catch(Exception e)
			{
				System.out.println("A* heurisitic not found");
			}
		
		}
		homework ti = new homework();
		//ti.printGraph(hb, ls);
		switch(algo)
		{
		case "BFS":
			ti.bfs(hb,ls,hs.get(start),hs.get(goal));
			break;
		case "DFS":
			ti.dfs(hb,ls,hs.get(start),hs.get(goal));
			break;
		case "UCS":
			try{
				ti.ucs(hb,ls,hs.get(start),hs.get(goal));	
			}
			catch(NullPointerException e){
				System.out.println(start+goal);
			}	
			break;
		case "A*":
			ti.astar(hs,hb,ls,hs.get(start),hs.get(goal),h);
			break;
			
		}
	}
	public void astar(Hashtable<String,Integer> hs,Hashtable<Integer,String> hb,ArrayList<Node> ls,int start, int goal,int[] h)
	{
		int counter = 0;
		//System.out.println(Arrays.toString(h));
		int visited[] = new int[hb.size()];  //IMPROVE
		for(int i = 0; i < visited.length; i++)
		{
			visited[i] = -1;
		}
		int cost[] = new int[hb.size()];
		ArrayList<Integer> path = new ArrayList<Integer>();
		Comparator<Ufs> comparator = new CostComparator();
		PriorityQueue<Ufs> q = new PriorityQueue<Ufs>(10,comparator); //open list
		PriorityQueue<Ufs> c = new PriorityQueue<Ufs>(10,comparator); //close list
		int i = start;
		q.add(new Ufs(start,h[start],counter++));
		visited[start] = start;
		try
		{
		while(i != goal)
		{
			//FAILURE CONDITION
				Ufs x = q.remove(); //remove from open
				c.add(x);  			//put into the close queue
				//System.out.println(x.ve);
				Node t = ls.get(x.ve); //Expand x;
				while(t != null)
				{
					Ufs r = isIn(q,t.v);
					Ufs rc = isIn(c,t.v);
					if(r == null && rc == null)  //not in the open list and closed
					{
						visited[t.v] = x.ve;
						q.add(new Ufs(t.v,cost[x.ve]+t.cost+h[(t.v)],counter++));
						cost[t.v] = cost[x.ve]+t.cost;
						
					}
					if(r == null && rc != null) //not in open but in closed
					{
						if(rc.cost > (cost[x.ve] + t.cost + h[(t.v)]))
						{
							c.remove(rc);
							q.add(new Ufs(t.v,cost[x.ve]+t.cost+h[(t.v)],counter++));
							visited[t.v] = x.ve;
							cost[t.v] = cost[x.ve]+t.cost;
						}
					}
					if(r != null) // in the open list: update cost
					{
						if(r.cost > (cost[x.ve] +t.cost+h[(t.v)]))
						{
							q.remove(r);
							q.add(new Ufs(t.v,cost[x.ve]+t.cost+h[(t.v)],counter++));
							visited[t.v] = x.ve;
							cost[t.v] = cost[x.ve]+t.cost;
						}
					}
					t = t.next;
				}	
			
			i = q.peek().ve;
		}
		}
		catch(Exception e)
		{
			System.out.println("Failure");
		}
		int t = goal;	
		int s;
		do
		{
			path.add(t);
			s = t;
			t = visited[t];
		}while(s != visited[t]);
		
		try
		{
			PrintWriter w = new PrintWriter(new File("output.txt"));
			int count = 0;
			for(int k = path.size() - 1; k >= 0; k--)
			{
				//count += cost[path.get(k)];
				//System.out.println(count+"->"+cost[path.get(k)]);
				w.println(hb.get(path.get(k))+" "+cost[path.get(k)]);
			}
			w.close();
			}
		catch(Exception e)
		{
			System.out.println("File Error");
		}
	}
	public void ucs(Hashtable<Integer,String> hb,ArrayList<Node> ls,int start, int goal)
	{
		int counter = 0;
		int visited[] = new int[hb.size()];  //IMPROVE
		for(int i = 0; i < visited.length; i++)
		{
			visited[i] = -1;
		}
		int cost[] = new int[hb.size()];
		ArrayList<Integer> path = new ArrayList<Integer>();
		Comparator<Ufs> comparator = new CostComparator();
		PriorityQueue<Ufs> q = new PriorityQueue<Ufs>(10,comparator); //open list
		PriorityQueue<Ufs> c = new PriorityQueue<Ufs>(10,comparator); //close list
		int i = start;
		q.add(new Ufs(start,0,counter++));
		visited[start] = start;
		try
		{
		while(i != goal)
		{
			//FAILURE CONDITION
				Ufs x = q.remove(); //remove from open
				c.add(x);  			//put into the close queue
				Node t = ls.get(x.ve); //Expand x;
				while(t != null)
				{
					Ufs r = isIn(q,t.v);
					Ufs rc = isIn(c,t.v);
					if(r == null && rc == null)  //not in the open list and closed
					{
						visited[t.v] = x.ve;
						q.add(new Ufs(t.v,cost[x.ve]+t.cost,counter++));
						cost[t.v] = cost[x.ve]+t.cost;
						
					}
					if(r == null && rc != null) //not in open but in closed
					{
						if(rc.cost > (cost[x.ve] + t.cost))
						{
							c.remove(rc);
							q.add(new Ufs(t.v,cost[x.ve]+t.cost,counter++));
							visited[t.v] = x.ve;
							cost[t.v] = cost[x.ve]+t.cost;
						}
					}
					if(r != null) // in the open list: update cost
					{
						if(r.cost > (cost[x.ve] + t.cost))
						{
							//System.out.println("Update");
							q.remove(r);
							q.add(new Ufs(t.v,cost[x.ve]+t.cost,counter++));
							visited[t.v] = x.ve;
							cost[t.v] = cost[x.ve]+t.cost;
						}
					}
					t = t.next;
				}	
			
			i = q.peek().ve;
		}
		}
		catch(Exception e)
		{
			System.out.println("Failure");
		}
		//System.out.println(Arrays.toString(visited));
		int t = goal;	
		int s;
		do
		{
			path.add(t);
			s = t;
			t = visited[t];
		}while(s != visited[t]);
		//System.out.println(Arrays.toString(path.toArray()));
		try
		{
			PrintWriter w = new PrintWriter(new File("output.txt"));
			int count = 0;
			for(int k = path.size() - 1; k >= 0; k--)
			{
				//count += cost[path.get(k)];
				//System.out.println(count+"->"+cost[path.get(k)]);
			//	System.out.println(hb.get(path.get(k))+" "+cost[path.get(k)]);
				w.println(hb.get(path.get(k))+" "+cost[path.get(k)]);
			}
			w.close();
			}
		catch(Exception e)
		{
			System.out.println("File Error");
		}
	}
	public Ufs isIn(PriorityQueue<Ufs> q,int v)
	{
		Ufs s;
		Iterator<Ufs> h = q.iterator();
		while(h.hasNext())
		{
			s = h.next();
			if(s.ve == v)
				return s;
		}
		return null;
	}
	public void bfs(Hashtable<Integer,String> hb,ArrayList<Node> ls,int start, int goal)
	{
		int visited[] = new int[hb.size()];  //IMPROVE
		for(int i = 0; i < visited.length; i++)
		{
			visited[i] = -1;
		}
		ArrayList<Integer> path = new ArrayList<Integer>();
		//path.add(start);
		Queue<Integer> q = new LinkedList<Integer>();
		int i = start;
		q.add(start);
		visited[start] = start;
		//System.out.println(Arrays.toString(visited));
		try
		{
		while(i != goal)
		{
			//FAILURE CONDITION
				int x = q.remove();
				Node t = ls.get(x); //Expand x;
				while(t != null)
				{
					if(visited[t.v] == -1) 
					{
						visited[t.v] = x;
						//System.out.println(Arrays.toString(visited));
						q.add(t.v);
					}
					t = t.next;
				}	
			
			i = q.element();
		}
		}
		catch(Exception e)
		{
			System.out.println("Failure");
		}
		//System.out.println(Arrays.toString(visited));
		int t = goal;	
		int s;
		do
		{
			path.add(t);
			s = t;
			t = visited[t];
		}while(s != visited[t]);
		// System.out.println(Arrays.toString(path.toArray()));
		int count = 0;
		try
		{
			PrintWriter w = new PrintWriter(new File("output.txt"));
			for(int k = path.size() - 1; k >= 0; k--)
			{
				w.println(hb.get(path.get(k))+" "+count);
				count++;
			}
			w.close();
			}
		catch(Exception e)
		{
			System.out.println("File Error");
		}
		
	}
	public void dfs(Hashtable<Integer,String> hb,ArrayList<Node> ls,int start, int goal)
	{
		int visited[] = new int[hb.size()];  //IMPROVE
		int parent[] = new int[hb.size()];
		for(int i = 0; i < visited.length; i++)
		{
			visited[i] = -1;
			parent[i] = -1;
		}
		ArrayList<Integer> path = new ArrayList<Integer>();
		Stack<Integer> q = new Stack<Integer>();
		Stack<Integer> p = new Stack<Integer>();
		int i = start;
		p.add(start);
		visited[start] = 0;
		parent[start] = start;
		try
		{
		while(i != goal)
		{
			//FAILURE CONDITION
			int x = p.pop();
			visited[x] = 0;
			Node t = ls.get(x); //Expand x;
			//reverse all nodes
			while(t != null)
			{
				if(visited[t.v] == -1 && p.search(t.v) == -1)
				{
					parent[t.v] = x;
					q.push(t.v);
				}
				t = t.next;
			}
			while(!q.isEmpty())
			{
				p.push(q.pop());
			}
		
		i = p.peek();
		}
		}
		catch(Exception e)
		{
			System.out.println("Failure");
		}
		int t = goal;	
		int s;
		do
		{
			path.add(t);
			s = t;
			t = parent[t];
		}while(s != parent[t]);
		int count = 0;
		try
		{
			PrintWriter w = new PrintWriter(new File("output.txt"));
			for(int k = path.size() - 1; k >= 0; k--)
			{
				w.println(hb.get(path.get(k))+" "+count);
				count++;
			}
			w.close();
			}
		catch(Exception e)
		{
			System.out.println("File Error");
		}
	}
	public void printGraph(Hashtable<Integer,String> hs,ArrayList<Node> ls)
	{
		for(int i = 0; i < ls.size(); i++)
		{
			System.out.print(hs.get(i)+" -> ");
			Node t = ls.get(i);
			while(t != null)
			{
				System.out.print(hs.get(t.v)+" "+t.cost+" -> ");
				t = t.next;
			}
			System.out.println("");
		}
	}

}
class Node
{
	Node(int vert,int c)
	{
		v = vert;
		cost = c;
		next = null;
	}
	int v;
	int cost;
	Node next;
}
class Ufs
{
	Ufs(int vert, int c,int t)
	{
		ve = vert;
		cost = c;	
		timestamp = t;
	}
	int ve;
	int cost;
	int timestamp;
}

class CostComparator implements Comparator<Ufs>
{
	public int compare(Ufs x, Ufs y)
	{
		if(x.cost > y.cost)
		{
			return 1;
		}
		else if(x.cost < y.cost)
		{
			return -1;
		}
		else
		{
			return x.timestamp - y.timestamp;
		}
	}
}


