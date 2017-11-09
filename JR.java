import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class JR 
{

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int n = Integer.parseInt(reader.readLine());
		String S;
		
		for(int i=0;i<n;i++)
		{
			S = reader.readLine();
			while(S.length() == 0)
			{	
				S = reader.readLine();
			}
			
			String[] splited = S.split("\\s+");
			int[] vred = new int[3];
			for(int kk=0;kk<3;kk++){vred[kk]= Integer.parseInt(splited[kk]);}
			char[][] matrika = new char[vred[1]][vred[0]];
			
			for(int j=0;j<vred[1];j++)
			{	
				S = reader.readLine();
				char[] Z = S.toCharArray();
				matrika[j] = Z;
			}
			
			//iskanje rešitve prebranega testa
			Maze MM = new Maze();
			MM.ustvari(matrika, vred[2]);
			System.out.println(MM.najdiNajMoc());
		}
		
		reader.close();
		
	}
	
	private static class Maze
	{
		private Vozlisce vitez;
		private Vozlisce zmaj;
		private Vozlisce [][] maze;
		private int najMOC = 0;
		private int L = 0;
		private LinkedList<Vozlisce[]> vrsta;
		
		public void ustvari(char[][] matrika,int L)
		{
			this.L = L;
			maze = new Vozlisce[matrika.length][matrika[0].length];
			for(int i=0;i<matrika.length;i++)
			{
				for(int j = 0; j<matrika[0].length;j++)
				{
					maze[i][j] = new Vozlisce();
					Vozlisce tre = maze[i][j];
					tre.nastaviVrednost(matrika[i][j]);
					tre.nastaviPozicijo(new int[]{i,j});
					this.nastaviSosede(tre);
					if(tre.vrednost == 'V')
					{
						this.vitez = tre;
					}
					if(tre.vrednost == 'Z')
					{
						this.zmaj = tre;
					}
				}
			}
		}

		void nastaviSosede(Vozlisce tre) 
		{
			if (tre.vrednost != '#')
			{
				int i = tre.pozicija[0];
				int j = tre.pozicija[1];
				if(i-1 >=0)
				{
					if (this.neprazen(i-1,j))
					{
						tre.nastaviSoseda(this.maze[i-1][j], 1);
					}
				}
				if(j-1>=0)
				{
					if(this.neprazen(i, j-1))
					{
						tre.nastaviSoseda(this.maze[i][j-1], 0);
					}
				}
				
				
			}
			else
			{
				return;
			}
		}

		boolean neprazen(int i, int j) 
		{
			//ta preverja kadar povezujemo (tudi zmaj mora biti povezan)
			char t = this.maze[i][j].vrednost;
			if(t != '#')
			{
				return true;
			}
			return false;
		}
	
		boolean dovoljen(Vozlisce V,int smer)
		{	//ta preverja ko iščemo po GRAFU čez zmaja ne smemo
			if(V.sosedje[smer] != null) 
			{
				char t = V.sosedje[smer].vrednost;
				if ((t != '#') && (t != 'Z'))
				{
					return true;
				}
			}
			return false;
		}
		
		void nastaviMoc()
		{
			for(int i=0;i<4;i++)
			{
				if (this.zmaj.sosedje[i] != null)
				{
					this.zmaj.sosedje[i].moc = 1;	
					for(int j=0;j<4;j++)
					{
						if(j != obratnaSmer(i))
						{
							nastaviMocRavno(this.zmaj.sosedje[i], j);
						}
					}
				}
				
			}
		}

		void nastaviMocRavno(Vozlisce predhodnik, int smer) 
		{
			if (this.dovoljen(predhodnik,smer)) //preveri smo na dovoljenem voz
			{
				predhodnik.sosedje[smer].moc = predhodnik.moc + 1;
				//predhodnik.sosedje[smer].korakov = predhodnik.korakov + 1;
				nastaviMocRavno(predhodnik.sosedje[smer], smer);
			}
			
		}
		
		void BFS() 
		{
			while(!vrsta.isEmpty())
			{
				
				Vozlisce[] A = vrsta.remove();
				Vozlisce predhodnik = A[0];
				Vozlisce sedanjik = A[1];
				
	
				if (sedanjik.obiskan)
				{
					
				}
				else
				{
					sedanjik.obisci();
					sedanjik.korakov = predhodnik.korakov + 1;
					if (sedanjik.moc > this.najMOC)
					{
						if ((sedanjik.moc + sedanjik.korakov -1)<= L )
						{
							this.najMOC = sedanjik.moc;
						}
					}
					if (sedanjik.korakov < L)
					{
						for(Vozlisce S:sedanjik.sosedje)
						{
							if(S != null)
							{
								vrsta.add(new Vozlisce[]{sedanjik,S});
							}
						}
					}	
				}
			}
		}
		
		public int najdiNajMoc()
		{
			
			nastaviMoc();
			this.vitez.obisci();
			this.zmaj.obisci();
			this.najMOC = this.vitez.moc;
			vrsta = new LinkedList<Vozlisce[]>();
			
			for(Vozlisce S : this.vitez.sosedje)
			{
				if(S != null)
				{
					vrsta.add(new Vozlisce[]{this.vitez,S});
					
				}
			}
			BFS();
			
			return this.najMOC;
		}

	}
	
	private static class Vozlisce
	{
		boolean obiskan = false;
		Vozlisce[] sosedje = new Vozlisce[4];
		public char vrednost;
		int[] pozicija;
		int moc = 0;
		int korakov = 0;
		@Override
		 public String toString() {
		    return pozicija[0] +","+ pozicija[1] + " "+ obiskan;
		}
		
		public void obisci()
		{
			this.obiskan = true;
		}
		
		public void nastaviPozicijo(int[] pozicija)
		{
			this.pozicija = pozicija;
		}
		
		public void nastaviVrednost(char vrednost)
		{
			this.vrednost = vrednost;
		}
		
		public void nastaviSoseda(Vozlisce S, int smer)
		{	
			if (this.sosedje[smer] == null)
			{
				this.sosedje[smer] = S;
				S.nastaviSoseda(this, obratnaSmer(smer));
			}
		}
		
	}

	public static int obratnaSmer(int smer) 
	{
		return (~smer)&3;
	}

	
}
