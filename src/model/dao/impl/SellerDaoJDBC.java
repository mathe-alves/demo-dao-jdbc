package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) { //conectando
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) { //inserir
		
		PreparedStatement st = null;
		try {
			
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+"(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+"VALUES "
					+"(?, ?, ?, ?, ?) ",
					Statement.RETURN_GENERATED_KEYS); //return_gene... é para retorna o id do novo vendedor inserido
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);//atribuimos um id gerado dentro do objeto obj, para que esse objeto fique populado com o novo id dele
				}
				DB.closeResultSet(rs);
		}
			else {
				throw new DbException("Unexpected error!  No rows affected!");
		}
	}
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
			finally {
				DB.closeStatement(st);
			}
		}
	

	@Override
	public void update(Seller obj) { //modificar
		PreparedStatement st = null;
		try {
			
			st = conn.prepareStatement(
					"UPDATE seller "
					+" SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+"WHERE Id = ? ");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
			
	}
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
			finally {
				DB.closeStatement(st);
			}
		
	}

	@Override
	public void deleteById(Integer id) { //deletar
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"DELETE FROM seller "
                    +"WHERE Id = ?");
			
			st.setInt(1, id);
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Seller findById(Integer id) { //visualizar
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
							+ "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id "
							+ "WHERE seller.Id = ?");
		
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) {
				
				Department dep = instantiateDepartment(rs); //instanciar o departamento
				
				Seller obj = instantiateSeller(rs, dep); //instanciar o vendedor
				
				return obj;
				
			}
			return null;
			
			}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	//reutilizando a instancianção de Department, melhorando o código
	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	//reutilizando a instancianção de Seller, melhorando o código
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() { //vê todos os vendedores com o nome do departamento
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+"FROM seller INNER JOIN department "
					+"ON seller.DepartmentId = department.Id "
					+"ORDER BY Name");
		
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			
			//chave integer e o valor de cada obj vai ser do tipo Departamento
			Map<Integer, Department> map = new HashMap<>();//vamos utilizar o map para não ficar repetindo o departamento
			
			while(rs.next()) { //enquanto tiver algo dentro, vai continuar rodando
				
				//dentro do Department, o map vai navegar para achar o id o Department
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				//se não achar nada no department vai retornar nulo e entrar no 'if' 
				//causando uma instanciação para colocar o valor do dep no map			
				if (dep == null) {
					dep = instantiateDepartment(rs);//instanciamos o department para colocar dentro do map, para que na próxima vez possa verificar e ver que existe
					map.put(rs.getInt("DepartmentId"), dep);//pra guardar no map. 'valor de chave / departamento q vai salvar'
				}
				
				Seller obj = instantiateSeller(rs, dep);//instanciar o vendedor
				
				list.add(obj); //adicionar o vendedor na lista
				
			}
			return list; //depois que percorrer tudo e adicionar todos os vendedores na lista, vai retornar essa lista
			
			}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	

	@Override
	public List<Seller> findByDepartment(Department department) { //vê todos os vendedores por departamento

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+"FROM seller INNER JOIN department "
					+"ON seller.DepartmentId = department.Id "
					+"WHERE DepartmentId = ? "
					+"ORDER BY Name");
		
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			
			//chave integer e o valor de cada obj vai ser do tipo Departamento
			Map<Integer, Department> map = new HashMap<>();//vamos utilizar o map para não ficar repetindo o departamento
			
			while(rs.next()) { //enquanto tiver algo dentro, vai continuar rodando
				
				//dentro do Department, o map vai navegar para achar o id o Department
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				//se não achar nada no department vai retornar nulo e entrar no 'if' 
				//causando uma instanciação para colocar o valor do dep no map			
				if (dep == null) {
					dep = instantiateDepartment(rs);//instanciamos o department para colocar dentro do map, para que na próxima vez possa verificar e ver que existe
					map.put(rs.getInt("DepartmentId"), dep);//pra guardar no map. 'valor de chave / departamento q vai salvar'
				}
				
				Seller obj = instantiateSeller(rs, dep);//instanciar o vendedor
				
				list.add(obj); //adicionar o vendedor na lista
				
			}
			return list; //depois que percorrer tudo e adicionar todos os vendedores na lista, vai retornar essa lista
			
			}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
