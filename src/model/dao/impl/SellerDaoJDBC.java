package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller obj) { //modificar
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) { //deletar
		// TODO Auto-generated method stub
		
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
