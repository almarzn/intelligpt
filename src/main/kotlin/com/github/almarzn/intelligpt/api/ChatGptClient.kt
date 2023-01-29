package com.github.almarzn.intelligpt.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.almarzn.intelligpt.services.AppSettingsState
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.objectBody
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.map

class ChatGptClient {
    private val defaultParams = Params(
            model = "text-davinci-003",
            maxTokens = 64,
            temperature = .2
    )

    fun generateResponse(prompt: String): String {
        val apiKey = AppSettingsState.instance.apiKey
        if (apiKey.isNullOrBlank()) {
            throw NoApiKeyConfiguredException()
        }
        val (_, _, result) = Fuel.post("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer $apiKey")
                .objectBody(defaultParams.copy(prompt = prompt))
                .responseObject<Response>()

        return result.map { it.choices.first().text }
                .get()!!
    }


    data class Params(
            @JsonProperty("model")
            var model: String,
            @JsonProperty("prompt")
            var prompt: String? = null,
            @JsonProperty("max_tokens")
            var maxTokens: Int? = null,
            @JsonProperty("temperature")
            var temperature: Double? = null,
            @JsonProperty("top_p")
            var topP: Int? = null,
            @JsonProperty("n")
            var n: Int? = null,
            @JsonProperty("stream")
            var stream: Boolean? = null,
            @JsonProperty("logprobs")
            var logprobs: String? = null,
            @JsonProperty("stop")
            var stop: String? = null
    )


    data class Response(
            @JsonProperty("id") var id: String? = null,
            @JsonProperty("object") var objectName: String? = null,
            @JsonProperty("created") var created: Int? = null,
            @JsonProperty("model") var model: String? = null,
            @JsonProperty("choices") var choices: List<Choices> = emptyList(),
            @JsonProperty("usage") var usage: Usage? = Usage()
    )

    data class Choices(

            @JsonProperty("text") var text: String? = null,
            @JsonProperty("index") var index: Int? = null,
            @JsonProperty("logprobs") var logprobs: String? = null,
            @JsonProperty("finish_reason") var finishReason: String? = null

    )

    data class Usage(

            @JsonProperty("prompt_tokens") var promptTokens: Int? = null,
            @JsonProperty("completion_tokens") var completionTokens: Int? = null,
            @JsonProperty("total_tokens") var totalTokens: Int? = null

    )
}