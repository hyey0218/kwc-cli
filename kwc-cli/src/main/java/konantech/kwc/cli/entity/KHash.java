package konantech.kwc.cli.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
@Table(name = "kwc_hash")
public class KHash {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int pk;
	int	idx;
	String hashed;
}
