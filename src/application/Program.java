package application;

import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
	
		System.out.println("=== TEST 1: Seller findById ===");
		Seller seller = sellerDao.findById(2);
		System.out.println(seller);
		
		System.out.println();

		System.out.println("=== TEST 2: Seller findByDepartment ===");
		Department department = new Department(2, null);
		List<Seller> list = sellerDao.findByDepartment(department);
		for (Seller obj : list) { //para cada Seller obj na minha Lista list
			System.out.println(obj);
		}
		
		System.out.println();

		System.out.println("=== TEST 3: Seller findAll ===");
	    list = sellerDao.findAll();
		for (Seller obj : list) { //para cada Seller obj na minha Lista list
			System.out.println(obj);
		}
		
		System.out.println();

		System.out.println("=== TEST 4: Seller insert ===");
		Seller newSeller = new Seller(null, "Greg", "greg@gmail.com", new Date(), 4000.0, department);
		sellerDao.insert(newSeller);
		System.out.println("Insered! New id = " + newSeller.getId());
		
		System.out.println();

		System.out.println("=== TEST 5: Seller update ===");
		seller = sellerDao.findById(1); //pegou o id do vendedor
		seller.setName("Martha Waine"); //alterou o nome do vendedor
		sellerDao.update(seller); //salvou a modificação
		System.out.println("Update complete!");
	}

}
