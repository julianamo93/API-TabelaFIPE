package br.com.tabelafipe.alura.TabelaFipe.Principal;

import br.com.tabelafipe.alura.TabelaFipe.Model.Dados;
import br.com.tabelafipe.alura.TabelaFipe.Model.Modelos;
import br.com.tabelafipe.alura.TabelaFipe.Model.Veiculo;
import br.com.tabelafipe.alura.TabelaFipe.Service.ConsumoApi;
import br.com.tabelafipe.alura.TabelaFipe.Service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Principal {

    private Scanner read = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        String opcao;

        do {
            var menu = """
                    \n>>> OPÇÕES DE CONSULTA FIPE <<<
                    1 - Carro
                    2 - Moto
                    3 - Caminhão
                    4 - Sair
                                    
                    Digite uma das opções para consultar: 
                    """;
            System.out.println(menu);
            String endereco = "";

            opcao = read.next();

            switch (opcao) {
                case "1":
                    System.out.println("Consulta de carros: ");
                    endereco = URL_BASE + "carros/marcas";
                    break;
                case "2":
                    System.out.println("Consulta de motos: ");
                    endereco = URL_BASE + "motos/marcas";
                    break;
                case "3":
                    System.out.println("Consulta de caminhões: ");
                    endereco = URL_BASE + "caminhoes/marcas";
                    break;
                case "4":
                    System.out.println("Saindo da consulta... Até a próxima!");
                    break;
                default:
                    System.out.println("Opção inválida! Escolha uma das opções de 1 a 4");
                    continue;
            }

            if (opcao.equals("4")) {
                break; // Sai do loop do-while se a opção for "4"
            }

            var json = consumoApi.obterDados(endereco);
            System.out.println(json);
            var marcas = conversor.obterLista(json, Dados.class);
            marcas.stream()
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("Informe o código da marca para consulta: ");
            var codigoMarca = read.next();

            endereco = endereco + "/" + codigoMarca + "/modelos";
            json = consumoApi.obterDados(endereco);
            var modeloLista = conversor.obterDados(json, Modelos.class);

            System.out.println("\nModelos dessa marca: ");
            modeloLista.modelos().stream()
                    .sorted(Comparator.comparing(Dados::codigo))
                    .forEach(System.out::println);

            System.out.println("\nDigite um trecho do nome do carro para a busca: ");
            var nomeVeiculo = read.next();

            List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                    .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                    .collect(Collectors.toList());

            System.out.println("\nModelos Filtrados:");
            modelosFiltrados.forEach(System.out::println);

            System.out.println("\nAgora digite o código do modelo para buscar as avaliações do veículo: ");
            var codigoModelo = read.next();

            endereco = endereco + "/" + codigoModelo + "/anos";
            json = consumoApi.obterDados(endereco);
            List<Dados> anos = conversor.obterLista(json, Dados.class);
            List<Veiculo> veiculos = new ArrayList<>();

            for (int i = 0; i < anos.size(); i++) {
                var enderecoAnos = endereco + "/" + anos.get(i).codigo();
                json = consumoApi.obterDados(enderecoAnos);
                Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
                veiculos.add(veiculo);
            }

            System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
            veiculos.forEach(System.out::println);

        } while (!opcao.equals("4"));
    }
}
