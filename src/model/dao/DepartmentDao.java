package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {
	
	void insert(Department obj); //aqui vai inserir no banco de dados o obj que enviar como parametro de entrada
	void update(Department obj);
	void deleteById(Integer id);
	Department findById(Integer id);//aqui vai pegar o id e consultar os dados desse id no banco de dados
	List<Department> findAll();//aqui vai pegar todos os departamentos

}
