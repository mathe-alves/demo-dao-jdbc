package model.dao;

import java.util.List;

import model.entities.Seller;

public interface SellerDao {
	
	void insert(Seller obj); //aqui vai inserir no banco de dados o obj que enviar como parametro de entrada
	void update(Seller obj);
	void deleteById(Integer id);
	Seller findById(Integer id);//aqui vai pegar o id e consultar os dados desse id no banco de dados
	List<Seller> findAll();//aqui vai pegar todos os vendedores


}
