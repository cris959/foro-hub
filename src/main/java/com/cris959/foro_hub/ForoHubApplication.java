package com.cris959.foro_hub;
/*
Copyright 2026 Christian Garay

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
 (exclude = {
		org.springframework.ai.autoconfigure.mistralai.MistralAiAutoConfiguration.class,
		org.springframework.ai.autoconfigure.chat.observation.ChatObservationAutoConfiguration.class,
		org.springframework.ai.autoconfigure.image.observation.ImageObservationAutoConfiguration.class,
		org.springframework.ai.autoconfigure.vectorstore.observation.VectorStoreObservationAutoConfiguration.class
})
public class ForoHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForoHubApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner testMistral(@Qualifier("mistralChatModel") ChatModel mistralChatModel) {
//		return args -> {
//			try {
//				System.out.println("--- Probando conexión con Mistral (Constructor 6 params) ---");
//
//				// Usamos un Prompt explícito con una lista de herramientas vacía
//				var prompt = new org.springframework.ai.chat.prompt.Prompt(
//						"Responde solo: CONECTADO",
//						org.springframework.ai.mistralai.MistralAiChatOptions.builder()
//								.toolCallbacks(java.util.List.of())
//								.build()
//				);
//
//				var response = mistralChatModel.call(prompt);
//				System.out.println("Resultado: " + response.getResult().getOutput().getText());
//
//			} catch (Exception e) {
//				System.err.println("Error en ejecución: " + e.getMessage());
//				// Si el error persiste, imprime esto para ver si es un 401 o 404
//				// e.printStackTrace();
//			}
//		};
//	}
}
